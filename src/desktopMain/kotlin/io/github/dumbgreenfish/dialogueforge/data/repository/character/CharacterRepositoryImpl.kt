package io.github.dumbgreenfish.dialogueforge.data.repository.character

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.dumbgreenfish.dialogueforge.data.model.TavernCardData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single(binds = [CharacterRepository::class])
class CharacterRepositoryImpl : CharacterRepository {
    private val db = run {
        val osName = System.getProperty("os.name").lowercase()
        val appDataDir = when {
            osName.contains("win") -> System.getenv("AppData")?.let { java.io.File(it) }
            osName.contains("mac") -> java.io.File(System.getProperty("user.home"), "Library/Application Support")
            else -> java.io.File(System.getProperty("user.home"), ".local/share")
        } ?: java.io.File(System.getProperty("user.home"))

        val dbDir = appDataDir.resolve("DialogueForge").resolve(CHARACTER_DB_DIR).also { it.mkdirs() }
        Room.databaseBuilder<CharacterDatabase>(
            name = dbDir.resolve(CHARACTER_DB_NAME).absolutePath
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    override val characters: Flow<List<CharacterEntity>>
        get() = db.characterDao().getAllFlow()

    override suspend fun getById(id: String) = db.characterDao().getById(id)

    override suspend fun import(data: TavernCardData) = db.characterDao().insert(data.toEntity())

    override suspend fun delete(id: String) = db.characterDao().delete(id)

    override suspend fun togglePin(id: String) {
        val entity = db.characterDao().getById(id) ?: return
        db.characterDao().update(entity.copy(pinned = !entity.pinned))
    }
}
