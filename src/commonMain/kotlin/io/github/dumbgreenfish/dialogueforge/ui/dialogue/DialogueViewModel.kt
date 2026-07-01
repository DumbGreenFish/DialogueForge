package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.DialogueRepository
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.toCharacter
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.toMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class DialogueViewModel(
    private val characterRepository: CharacterRepository,
    private val dialogueRepository: DialogueRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(DialogueState())
    val state: StateFlow<DialogueState> = _state.asStateFlow()

    fun handle(intent: DialogueIntent) {
        when (intent) {
            is DialogueIntent.LoadCharacter -> loadCharacter(intent.id)
            is DialogueIntent.LoadConversation -> loadConversation()
            is DialogueIntent.Back -> {}
            is DialogueIntent.UpdateInput -> _state.update { it.copy(inputText = intent.text) }
            is DialogueIntent.Send -> onSend()
        }
    }

    private fun onSend() {
        val text = _state.value.inputText.trim()
        if (text.isEmpty()) return
        val conversationId = _state.value.conversationId ?: return
        _state.update { it.copy(inputText = "") }
        viewModelScope.launch {
            dialogueRepository.addMessage(conversationId, "user", text)
        }
    }

    private fun loadCharacter(id: String) {
        if (_state.value.character?.id == id && _state.value.isLoading) return
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val entity = characterRepository.getById(id)
            val character = entity?.toCharacter()
            _state.update {
                it.copy(
                    character = character,
                    isLoading = false,
                )
            }
            if (character != null) {
                handle(DialogueIntent.LoadConversation)
            }
        }
    }

    private fun loadConversation() {
        val character = _state.value.character ?: return
        viewModelScope.launch {
            val conversation = dialogueRepository.getOrCreateConversation(
                characterId = character.id,
                greeting = character.firstMessage,
            )
            dialogueRepository.getMessages(conversation.id).collect { entities ->
                _state.update {
                    it.copy(
                        conversationId = conversation.id,
                        messages = entities.map { e -> e.toMessage() },
                    )
                }
            }
        }
    }
}
