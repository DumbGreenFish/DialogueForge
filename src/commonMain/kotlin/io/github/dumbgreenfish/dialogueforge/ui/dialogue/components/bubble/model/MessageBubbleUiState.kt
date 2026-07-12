package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.bubble.model

import androidx.compose.ui.text.input.TextFieldValue

internal data class MessageBubbleUiState(
    val isSelected: Boolean = false,
    val inSelectionMode: Boolean = false,
    val showActionRow: Boolean = false,
    val isEditing: Boolean = false,
    val isGenerating: Boolean = false,
    val editText: TextFieldValue = TextFieldValue(),
)
