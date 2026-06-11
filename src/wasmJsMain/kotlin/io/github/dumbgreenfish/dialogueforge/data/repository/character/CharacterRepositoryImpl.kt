package io.github.dumbgreenfish.dialogueforge.data.repository.character

import androidx.room3.Room
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import io.github.dumbgreenfish.dialogueforge.data.model.TavernCardData
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single
import org.w3c.dom.Worker

private const val SQLITE_WORKER_SCRIPT = "sqlite-worker.js"

@JsFun("(url) => new Worker(url, { type: 'module' })")
private external fun createModuleWorker(url: String): Worker

@Single(binds = [CharacterRepository::class])
class CharacterRepositoryImpl : CharacterRepository {
    private val db = Room.databaseBuilder<CharacterDatabase>(name = CHARACTER_DB_NAME)
        .setDriver(WebWorkerSQLiteDriver(createModuleWorker(SQLITE_WORKER_SCRIPT)))
        .build()

    override val characters: Flow<List<CharacterEntity>>
        get() = db.characterDao().getAllFlow()

    override suspend fun import(data: TavernCardData) = db.characterDao().insert(data.toEntity())

    override suspend fun delete(id: String) = db.characterDao().delete(id)

    override suspend fun togglePin(id: String) {
        val entity = db.characterDao().getById(id) ?: return
        db.characterDao().update(entity.copy(pinned = !entity.pinned))
    }
}
