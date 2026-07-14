package io.github.dumbgreenfish.dialogueforge.ui.dialogue.model

import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.MessageEntity

internal fun MessageEntity.toMessage(variantCount: Int = 1): Message = Message(
    id = id,
    role = MessageRole.fromWire(role),
    text = text,
    timestamp = timestamp,
    variantIndex = activeVariant,
    variantCount = variantCount,
)
