package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.bubble.model

import androidx.compose.ui.text.input.TextFieldValue

internal data class MessageBubbleActions(
    val onToggleSelection: (String) -> Unit = {},
    val onEnterSelectionMode: (String) -> Unit = {},
    val onShowActionRow: (String) -> Unit = {},
    val onCopy: (String) -> Unit = {},
    val onEdit: (String) -> Unit = {},
    val onDelete: (String) -> Unit = {},
    val onSave: (String) -> Unit = {},
    val onCancel: () -> Unit = {},
    val onEditTextChange: (TextFieldValue) -> Unit = {},
)
