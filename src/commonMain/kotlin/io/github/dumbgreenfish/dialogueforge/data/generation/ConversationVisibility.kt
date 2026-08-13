package io.github.dumbgreenfish.dialogueforge.data.generation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.Single

@Single
class ConversationVisibility {
    private val _state = MutableStateFlow(ConversationVisibilityState())
    val state: StateFlow<ConversationVisibilityState> = _state.asStateFlow()

    fun setAppVisible(visible: Boolean) {
        _state.update { it.copy(appVisible = visible) }
    }

    fun showCharacter(characterId: String) {
        _state.update { it.copy(characterId = characterId) }
    }

    fun hideCharacter(characterId: String) {
        _state.update { current ->
            if (current.characterId == characterId) current.copy(characterId = null) else current
        }
    }
}

data class ConversationVisibilityState(
    val appVisible: Boolean = false,
    val characterId: String? = null,
)
