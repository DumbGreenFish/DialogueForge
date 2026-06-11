package io.github.dumbgreenfish.dialogueforge.data.repository.character

import io.github.dumbgreenfish.dialogueforge.data.model.TavernCardData
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    val characters: Flow<List<CharacterEntity>>
    suspend fun import(data: TavernCardData)
    suspend fun delete(id: String)
    suspend fun togglePin(id: String)
}
