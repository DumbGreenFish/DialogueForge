package io.github.dumbgreenfish.dialogueforge.ui.dialogue.model

enum class MessageRole(val wire: String) {
    System("system"),
    User("user"),
    Assistant("assistant");

    companion object {
        fun fromWire(value: String): MessageRole =
            entries.firstOrNull { it.wire == value } ?: System
    }
}
