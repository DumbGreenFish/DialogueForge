package io.github.dumbgreenfish.dialogueforge.ui.dialogue

sealed class DialogueIntent {
    data class LoadCharacter(val id: String) : DialogueIntent()
    data object LoadConversation : DialogueIntent()
    data object Back : DialogueIntent()
    data class UpdateInput(val text: String) : DialogueIntent()
    data object Send : DialogueIntent()
    data object DismissError : DialogueIntent()
    data object StopGeneration : DialogueIntent()
    data object Regenerate : DialogueIntent()
    data class DeleteMessage(val messageId: String) : DialogueIntent()
}
