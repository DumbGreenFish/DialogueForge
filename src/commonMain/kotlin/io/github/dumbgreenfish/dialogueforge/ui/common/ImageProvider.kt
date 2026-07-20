package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import io.github.dumbgreenfish.dialogueforge.data.cache.ImageCache
import kotlinx.coroutines.flow.StateFlow
import org.koin.compose.koinInject

interface ImageProvider {
    fun observe(maxDimension: Int): StateFlow<ImageBitmap?>
}

class DefaultImageProvider(
    private val cache: ImageCache,
    private val characterId: String,
) : ImageProvider {
    override fun observe(maxDimension: Int): StateFlow<ImageBitmap?> =
        cache.observe(characterId, maxDimension)
}

@Composable
fun rememberImageProvider(characterId: String): ImageProvider {
    val cache = koinInject<ImageCache>()
    return remember(characterId, cache) { DefaultImageProvider(cache, characterId) }
}
