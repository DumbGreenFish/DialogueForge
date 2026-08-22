package io.github.dumbgreenfish.dialogueforge.data.service

import kotlinx.serialization.Serializable

@Serializable
data class ChatDelta(
    val content: String? = null,
)
