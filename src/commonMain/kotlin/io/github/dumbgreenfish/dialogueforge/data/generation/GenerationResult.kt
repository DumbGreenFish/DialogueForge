package io.github.dumbgreenfish.dialogueforge.data.generation

sealed interface GenerationResult {
    data class Success(
        val characterId: String,
        val characterName: String,
        val avatar: ByteArray?,
        val response: String,
    ) : GenerationResult

    data object Failure : GenerationResult
}
