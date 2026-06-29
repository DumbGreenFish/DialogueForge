package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character

data class DialogueState(
    val character: Character? = null,
    val isLoading: Boolean = false,
    val inputText: String = "",
    val presetName: String = "",
    val modelName: String = "",
)
