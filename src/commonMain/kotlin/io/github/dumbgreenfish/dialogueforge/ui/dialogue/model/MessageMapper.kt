package io.github.dumbgreenfish.dialogueforge.ui.dialogue.model

import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.MessageEntity

internal fun MessageEntity.toMessage(): Message = Message(
    id = id,
    role = when (role) {
        "system" -> MessageRole.System
        "user" -> MessageRole.User
        "assistant" -> MessageRole.Assistant
        else -> MessageRole.System
    },
    text = text,
    timestamp = timestamp,
)
