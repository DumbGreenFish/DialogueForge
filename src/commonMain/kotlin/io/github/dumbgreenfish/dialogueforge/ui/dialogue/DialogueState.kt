package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import androidx.compose.ui.text.input.TextFieldValue
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message

data class DialogueState(
    val character: Character? = null,
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val error: String? = null,
    val inputText: TextFieldValue = TextFieldValue(),
    val presetName: String = "",
    val modelName: String = "",
    val messages: List<Message> = emptyList(),
    val conversationId: String? = null,
    val lastSentText: String? = null,
    val selectedMessageIds: Set<String> = emptySet(),
)
