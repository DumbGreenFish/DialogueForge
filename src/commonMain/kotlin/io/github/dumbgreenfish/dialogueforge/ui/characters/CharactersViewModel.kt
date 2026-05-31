package io.github.dumbgreenfish.dialogueforge.ui.characters

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class CharactersViewModel : ViewModel() {
    private val _state = MutableStateFlow(CharactersState())
    val state: StateFlow<CharactersState> = _state.asStateFlow()

    fun handle(intent: CharactersIntent) = when (intent) {
        is CharactersIntent.SearchChanged    -> _state.value = _state.value.copy(query = intent.query)
        is CharactersIntent.FilterChanged    -> _state.value = _state.value.copy(activeFilter = intent.filter)
        is CharactersIntent.ViewModeChanged  -> _state.value = _state.value.copy(viewMode = intent.mode)
    }
}
