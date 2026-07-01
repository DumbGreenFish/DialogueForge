package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message

data class DialogueState(
    val character: Character? = null,
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val error: String? = null,
    val inputText: String = "",
    val presetName: String = "",
    val modelName: String = "",
    val messages: List<Message> = emptyList(),
    val conversationId: String? = null,
    val lastSentText: String? = null,
)
