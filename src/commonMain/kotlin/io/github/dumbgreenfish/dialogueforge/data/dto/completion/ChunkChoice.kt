package io.github.dumbgreenfish.dialogueforge.data.dto.completion

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChunkChoice(
    val delta: ChatDelta = ChatDelta(),
    @SerialName("finish_reason") val finishReason: String? = null,
)
