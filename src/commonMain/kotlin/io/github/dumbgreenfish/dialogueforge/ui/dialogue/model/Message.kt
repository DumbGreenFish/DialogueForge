package io.github.dumbgreenfish.dialogueforge.ui.dialogue.model

data class Message(
    val id: String,
    val role: MessageRole,
    val text: String,
    val timestamp: Long,
)
