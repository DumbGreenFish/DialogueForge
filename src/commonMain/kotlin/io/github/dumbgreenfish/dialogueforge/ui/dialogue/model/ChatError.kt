package io.github.dumbgreenfish.dialogueforge.ui.dialogue.model

enum class ChatErrorType {
    NoApiKey,
    Network,
    Server,
    Unknown,
}

data class ChatError(
    val type: ChatErrorType,
    val details: String,
)
