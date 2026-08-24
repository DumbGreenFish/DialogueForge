package io.github.dumbgreenfish.dialogueforge.service.generation.api

data class GenerationRequest(
    val conversationId: String,
    val characterId: String,
    val userText: String?,
    val characterName: String = characterId,
)
