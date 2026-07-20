package io.github.dumbgreenfish.dialogueforge.data.cache

import androidx.compose.ui.graphics.ImageBitmap
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterRepository
import io.github.dumbgreenfish.dialogueforge.util.image.toImageBitmapOrNull
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single

@Single
class ImageCache(private val repo: CharacterRepository) {

    private val mutex = Mutex()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val byteCache = LinkedHashMap<String, ByteArray>()
    private val bitmapCache = LinkedHashMap<String, MutableStateFlow<ImageBitmap?>>()
    private val timestamps = mutableMapOf<String, Long>()
    private val pendingBitmapLoads = mutableSetOf<String>()
    private val pendingByteLoads = mutableMapOf<String, CompletableDeferred<ByteArray?>>()
    private val popupJobs = mutableMapOf<String, Job>()

    fun observe(characterId: String, maxDimension: Int): StateFlow<ImageBitmap?> {
        val key = "$characterId:$maxDimension"
        val cached = bitmapCache[key]
        if (cached != null) {
            scope.launch { mutex.withLock { touchBitmapCacheEntry(key) } }
            return cached
        }
        val flow = MutableStateFlow<ImageBitmap?>(null)
        scope.launch {
            mutex.withLock {
                val existing = bitmapCache[key]
                if (existing != null) {
                    touchBitmapCacheEntry(key)
                    return@launch
                }
                bitmapCache[key] = flow
                evictBitmapCacheIfNeeded()
            }
            loadBitmap(characterId, maxDimension, key, flow)
        }
        return flow
    }

    fun observeBackground(key: String): MutableStateFlow<ImageBitmap?> {
        val cacheKey = "bg:$key"
        val cached = bitmapCache[cacheKey]
        if (cached != null) {
            scope.launch { mutex.withLock { touchBitmapCacheEntry(cacheKey) } }
            return cached
        }
        val flow = MutableStateFlow<ImageBitmap?>(null)
        scope.launch {
            mutex.withLock {
                val existing = bitmapCache[cacheKey]
                if (existing != null) {
                    touchBitmapCacheEntry(cacheKey)
                    return@launch
                }
                bitmapCache[cacheKey] = flow
                evictBitmapCacheIfNeeded()
            }
        }
        return flow
    }

    fun invalidate(characterId: String) {
        scope.launch {
            mutex.withLock {
                timestamps.remove(characterId)
                byteCache.remove(characterId)
                val keysToRemove = bitmapCache.keys.filter { it.startsWith("$characterId:") }
                keysToRemove.forEach { bitmapCache.remove(it) }
            }
        }
    }

    fun preload(characterId: String, maxDimensions: List<Int>) {
        for (dim in maxDimensions) observe(characterId, dim)
    }

    fun observePopup(characterId: String, maxDimension: Int): StateFlow<ImageBitmap?> {
        popupJobs[characterId]?.cancel()

        val key = "$characterId:$maxDimension"
        val cached = bitmapCache[key]
        if (cached != null) {
            scope.launch { mutex.withLock { touchBitmapCacheEntry(key) } }
            return cached
        }
        val flow = MutableStateFlow<ImageBitmap?>(null)
        val job = scope.launch {
            mutex.withLock {
                val existing = bitmapCache[key]
                if (existing != null) {
                    touchBitmapCacheEntry(key)
                    flow.value = existing.value
                    return@launch
                }
                bitmapCache[key] = flow
                evictBitmapCacheIfNeeded()
            }
            loadBitmap(characterId, maxDimension, key, flow)
        }
        popupJobs[characterId] = job
        return flow
    }

    private fun touchBitmapCacheEntry(key: String) {
        bitmapCache.remove(key)?.let { bitmapCache[key] = it }
    }

    private fun evictBitmapCacheIfNeeded() {
        while (bitmapCache.size > MAX_BITMAP_ENTRIES) {
            bitmapCache.remove(bitmapCache.keys.first())
        }
    }

    private fun evictByteCacheIfNeeded() {
        while (byteCache.size > MAX_BYTE_ENTRIES) {
            byteCache.remove(byteCache.keys.first())
        }
    }

    private suspend fun loadBitmap(
        characterId: String,
        maxDimension: Int,
        key: String,
        flow: MutableStateFlow<ImageBitmap?>,
    ) {
        mutex.withLock {
            if (!pendingBitmapLoads.add(key)) return
        }
        try {
            val stubBytes = repo.getSizedThumbnail(characterId, maxDimension)
            val stubBitmap = stubBytes?.toImageBitmapOrNull()
            if (stubBitmap != null) {
                mutex.withLock { bitmapCache[key]?.value = stubBitmap }
            }

            val fullBytes = loadBytes(characterId)
            if (fullBytes != null) {
                val properBitmap = fullBytes.toImageBitmapOrNull(maxDimension)
                mutex.withLock { bitmapCache[key]?.value = properBitmap }
            }
        } finally {
            mutex.withLock { pendingBitmapLoads.remove(key) }
        }
    }

    private suspend fun loadBytes(characterId: String): ByteArray? {
        val (deferred, isCreator) = mutex.withLock {
            pendingByteLoads[characterId]?.let { return@withLock it to false }
            val d = CompletableDeferred<ByteArray?>()
            pendingByteLoads[characterId] = d
            d to true
        }

        if (!isCreator) return deferred.await()

        try {
            val cachedResult = mutex.withLock {
                byteCache.remove(characterId)?.let { value ->
                    byteCache[characterId] = value
                    return@withLock value
                }
            }
            if (cachedResult != null) {
                deferred.complete(cachedResult)
                return cachedResult
            }

            val entity = repo.getById(characterId)
            if (entity == null) {
                deferred.complete(null)
                return null
            }

            val ts = mutex.withLock { timestamps[characterId] }
            if (ts == entity.updatedAt) {
                val cached = mutex.withLock { byteCache[characterId] }
                if (cached != null) {
                    deferred.complete(cached)
                    return cached
                }
            }

            val bytes = repo.getMainImageThumbnail(characterId)
            mutex.withLock {
                if (bytes != null) {
                    byteCache[characterId] = bytes
                    evictByteCacheIfNeeded()
                    timestamps[characterId] = entity.updatedAt
                }
            }
            deferred.complete(bytes)
            return bytes
        } catch (e: Exception) {
            deferred.complete(null)
            throw e
        } finally {
            mutex.withLock { pendingByteLoads.remove(characterId) }
        }
    }

    companion object {
        private const val MAX_BYTE_ENTRIES = 300
        private const val MAX_BITMAP_ENTRIES = 250
    }
}
