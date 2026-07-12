package io.github.dumbgreenfish.dialogueforge.data.repository.character

import io.github.dumbgreenfish.dialogueforge.data.config.DatabaseConfig
import io.github.dumbgreenfish.dialogueforge.data.config.MainDatabase
import io.github.dumbgreenfish.dialogueforge.data.model.TavernCardData
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single(binds = [CharacterRepository::class])
class CharacterRepositoryImpl(dbConfig: DatabaseConfig) : CharacterRepository {

    private val db = dbConfig.mainDatabase()

    override val characters: Flow<List<CharacterEntity>> = db.characterDao().getAllFlow()

    override suspend fun getById(id: String) = db.characterDao().getById(id)

    override suspend fun import(data: TavernCardData) = db.characterDao().insert(data.toEntity())

    override suspend fun delete(id: String) = db.characterDao().delete(id)

    override suspend fun togglePin(id: String) = db.characterDao().togglePin(id)
}
