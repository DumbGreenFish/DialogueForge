package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import androidx.compose.ui.text.input.TextFieldValue

sealed class DialogueIntent {
    data class LoadCharacter(val id: String) : DialogueIntent()
    data object LoadConversation : DialogueIntent()
    data object Back : DialogueIntent()
    data class UpdateInput(val value: TextFieldValue) : DialogueIntent()
    data object Send : DialogueIntent()
    data object DismissError : DialogueIntent()
    data object StopGeneration : DialogueIntent()
    data object Regenerate : DialogueIntent()
    data class DeleteMessage(val messageId: String) : DialogueIntent()
    data class ToggleMessageSelection(val messageId: String) : DialogueIntent()
    data object ClearSelection : DialogueIntent()
    data object DeleteSelected : DialogueIntent()
}
