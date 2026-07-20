package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.ui.text.input.TextFieldValue

sealed interface MessageItemEvent {
    data object ToggleActions : MessageItemEvent
    data object ToggleSelection : MessageItemEvent
}

sealed interface MessageInteractionState {
    data class Browsing(val isActionsExpanded: Boolean = false) : MessageInteractionState
    data class Editing(val text: TextFieldValue) : MessageInteractionState
    data object Generating : MessageInteractionState
    data class Selecting(val isSelected: Boolean) : MessageInteractionState
}
