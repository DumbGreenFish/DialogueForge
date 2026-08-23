package io.github.dumbgreenfish.dialogueforge.data.dto.completion

import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionChunk(
    val choices: List<ChunkChoice> = emptyList(),
)
