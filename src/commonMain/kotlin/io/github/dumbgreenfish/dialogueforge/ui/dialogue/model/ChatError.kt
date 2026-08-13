package io.github.dumbgreenfish.dialogueforge.ui.dialogue.model

enum class ChatErrorType {
    NoApiKey,
    Network,
    Server,
    Interrupted,
    Unknown,
}

data class ChatError(
    val type: ChatErrorType,
    val details: String,
)
