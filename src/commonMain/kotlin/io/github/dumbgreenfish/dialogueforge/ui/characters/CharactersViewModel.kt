package io.github.dumbgreenfish.dialogueforge.ui.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dumbgreenfish.dialogueforge.data.format.ParseResult
import io.github.dumbgreenfish.dialogueforge.data.format.TavernCardParser
import io.github.dumbgreenfish.dialogueforge.data.model.DefaultCharacterData
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.ForgeSettings
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharacterFilter
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.toCharacter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class CharactersViewModel(
    private val repository: CharacterRepository,
    private val forgeSettings: ForgeSettings,
) : ViewModel() {
    private val _state = MutableStateFlow(CharactersState())
    val state: StateFlow<CharactersState> = _state.asStateFlow()

    init {
        forgeSettings.defaultViewMode
            .onEach { mode -> _state.update { it.copy(viewMode = mode) } }
            .launchIn(viewModelScope)
        repository.characters
            .onEach { entities -> _state.update { s -> s.copy(characters = entities.map { it.toCharacter() }) } }
            .launchIn(viewModelScope)
    }

    fun handle(intent: CharactersIntent) {
        when (intent) {
            is CharactersIntent.SearchChanged   -> _state.update { it.copy(query = intent.query) }
            is CharactersIntent.ViewModeChanged -> _state.update { it.copy(viewMode = intent.mode) }
            is CharactersIntent.ImportFile      -> viewModelScope.launch { importFile(intent) }
            is CharactersIntent.QuickFilterChanged -> updateFilter { it.copy(quick = intent.quick) }
            is CharactersIntent.IncludeTagAdded    -> updateFilter { it.copy(includeTags = (it.includeTags + intent.tag).distinct(), excludeTags = it.excludeTags - intent.tag) }
            is CharactersIntent.IncludeTagRemoved  -> updateFilter { it.copy(includeTags = it.includeTags - intent.tag) }
            is CharactersIntent.ExcludeTagAdded    -> updateFilter { it.copy(excludeTags = (it.excludeTags + intent.tag).distinct(), includeTags = it.includeTags - intent.tag) }
            is CharactersIntent.ExcludeTagRemoved  -> updateFilter { it.copy(excludeTags = it.excludeTags - intent.tag) }
            is CharactersIntent.FiltersReset       -> updateFilter { CharacterFilter() }
            is CharactersIntent.DeleteCharacter -> viewModelScope.launch { repository.delete(intent.id) }
            is CharactersIntent.DismissError -> _state.update { it.copy(error = null) }
            is CharactersIntent.ImportDefault -> viewModelScope.launch { importDefault() }
        }
    }

    private fun updateFilter(transform: (CharacterFilter) -> CharacterFilter) {
        _state.update { currentState -> currentState.copy(filter = transform(currentState.filter)) }
    }

    private suspend fun importFile(intent: CharactersIntent.ImportFile) {
        when (val result = TavernCardParser.parse(intent.bytes, intent.filename)) {
            is ParseResult.Success -> repository.import(result.data)
            is ParseResult.Failure -> _state.update { it.copy(error = result.message) }
        }
    }

    private suspend fun importDefault() {
        val data = DefaultCharacterData.create()
        if (data != null) {
            repository.import(data)
        } else {
            _state.update { it.copy(error = "Failed to load default character") }
        }
    }
}
