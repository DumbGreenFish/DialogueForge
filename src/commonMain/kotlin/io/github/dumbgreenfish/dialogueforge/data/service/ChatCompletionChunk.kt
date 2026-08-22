package io.github.dumbgreenfish.dialogueforge.data.service

import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionChunk(
    val choices: List<ChunkChoice> = emptyList(),
)
