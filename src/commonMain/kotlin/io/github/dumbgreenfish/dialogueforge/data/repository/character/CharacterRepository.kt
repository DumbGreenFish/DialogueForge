package io.github.dumbgreenfish.dialogueforge.data.repository.character

import io.github.dumbgreenfish.dialogueforge.data.model.TavernCardData
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    val characters: Flow<List<CharacterEntity>>
    suspend fun getById(id: String): CharacterEntity?
    suspend fun import(data: TavernCardData)
    suspend fun delete(id: String)
    suspend fun togglePin(id: String)
    suspend fun getMainImageThumbnail(id: String): ByteArray?
    suspend fun getFullMainImage(id: String): ByteArray?
    suspend fun getSizedThumbnail(id: String, maxDimension: Int): ByteArray?
}
