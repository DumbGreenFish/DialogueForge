package io.github.dumbgreenfish.dialogueforge.ui.dialogue

sealed class DialogueIntent {
    data class LoadCharacter(val id: String) : DialogueIntent()
    data object Back : DialogueIntent()
    data class UpdateInput(val text: String) : DialogueIntent()
    data object Send : DialogueIntent()
}
