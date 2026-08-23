package io.github.dumbgreenfish.dialogueforge.data.dto.completion

import kotlinx.serialization.Serializable

@Serializable
data class ChatDelta(
    val content: String? = null,
)
