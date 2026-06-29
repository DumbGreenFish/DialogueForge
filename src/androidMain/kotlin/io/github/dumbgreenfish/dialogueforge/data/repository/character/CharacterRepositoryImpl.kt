package io.github.dumbgreenfish.dialogueforge.data.repository.character

import android.app.Application
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.dumbgreenfish.dialogueforge.data.model.TavernCardData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single(binds = [CharacterRepository::class])
class CharacterRepositoryImpl(private val app: Application) : CharacterRepository {
    private val db = run {
        val dbFile = app.getDatabasePath(CHARACTER_DB_NAME)
        dbFile.parentFile?.mkdirs()
        Room.databaseBuilder<CharacterDatabase>(name = dbFile.absolutePath)
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
