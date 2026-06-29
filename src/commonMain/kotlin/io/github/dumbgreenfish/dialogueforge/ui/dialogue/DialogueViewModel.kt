package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterRepository
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.toCharacter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class DialogueViewModel(private val repository: CharacterRepository) : ViewModel() {
    private val _state = MutableStateFlow(DialogueState())
    val state: StateFlow<DialogueState> = _state.asStateFlow()

    fun handle(intent: DialogueIntent) {
        when (intent) {
            is DialogueIntent.LoadCharacter -> loadCharacter(intent.id)
            is DialogueIntent.Back -> {} // processed by onBack callback in View
            is DialogueIntent.UpdateInput -> _state.update { it.copy(inputText = intent.text) }
            is DialogueIntent.Send -> onSend()
        }
    }

    private fun onSend() {
        val text = _state.value.inputText.trim()
        if (text.isEmpty()) return
        _state.update { it.copy(inputText = "") }
        // TODO: not implemented — will send message via API layer
    }

    private fun loadCharacter(id: String) {
        if (_state.value.character?.id == id && _state.value.isLoading) return
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val entity = repository.getById(id)
            _state.update {
                it.copy(
                    character = entity?.toCharacter(),
                    isLoading = false,
                )
            }
        }
    }
}
