package io.github.dumbgreenfish.dialogueforge.data.generation

data class GenerationRequest(
    val conversationId: String,
    val characterId: String,
    val userText: String?,
    val characterName: String = characterId,
)
