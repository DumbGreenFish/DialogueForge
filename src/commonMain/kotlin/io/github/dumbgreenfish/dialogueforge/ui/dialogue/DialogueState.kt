package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import androidx.compose.ui.text.input.TextFieldValue
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatError
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message

data class DialogueState(
    val character: Character? = null,
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val inputText: TextFieldValue = TextFieldValue(),
    val presetName: String = "",
    val modelName: String = "",
    val messages: List<Message> = emptyList(),
    val conversationId: String? = null,
    val isLoadingOlder: Boolean = false,
    val hasMoreOlderMessages: Boolean = true,
    val expandedActionsMessageId: String? = null,
    val editingMessageId: String? = null,
    val editingText: TextFieldValue = TextFieldValue(),
    val selectedMessageIds: Set<String> = emptySet(),
    val chatError: ChatError? = null,
)
