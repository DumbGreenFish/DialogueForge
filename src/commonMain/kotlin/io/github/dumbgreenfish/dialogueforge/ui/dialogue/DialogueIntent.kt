package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import androidx.compose.ui.text.input.TextFieldValue

sealed class DialogueIntent {
    data class LoadCharacter(val id: String) : DialogueIntent()
    data object LoadConversation : DialogueIntent()
    data object Back : DialogueIntent()
    data class UpdateInput(val value: TextFieldValue) : DialogueIntent()
    data object Send : DialogueIntent()
    data object DismissSnackbar : DialogueIntent()
    data object StopGeneration : DialogueIntent()
    data object Regenerate : DialogueIntent()
    data class DeleteMessage(val messageId: String) : DialogueIntent()
    data object LoadOlderMessages : DialogueIntent()
    data class ToggleActions(val messageId: String) : DialogueIntent()
    data class StartEdit(val messageId: String) : DialogueIntent()
    data class UpdateEditText(val value: TextFieldValue) : DialogueIntent()
    data object SaveEdit : DialogueIntent()
    data object CancelEdit : DialogueIntent()
    data class RegenerateMessage(val messageId: String) : DialogueIntent()
    data class NextVariant(val messageId: String) : DialogueIntent()
    data class PrevVariant(val messageId: String) : DialogueIntent()
    data class ToggleSelection(val messageId: String) : DialogueIntent()
    data object ClearSelection : DialogueIntent()
    data object DeleteSelected : DialogueIntent()
}
