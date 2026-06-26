package io.github.dumbgreenfish.dialogueforge.ui.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dumbgreenfish.dialogueforge.data.format.ParseResult
import io.github.dumbgreenfish.dialogueforge.data.format.TavernCardParser
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterEntity
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterRepository
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharacterFilter
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.KoinViewModel

private const val TAGLINE_PREVIEW_LENGTH = 120
private val CHAR_PLACEHOLDERS = listOf("{{char}}", "<char>", "<bot>")

private fun String.substituteCharName(name: String): String =
    CHAR_PLACEHOLDERS.fold(this) { current, placeholder ->
        current.replace(placeholder, name, ignoreCase = true)
    }

@KoinViewModel
class CharactersViewModel(private val repository: CharacterRepository) : ViewModel() {
    private val _state = MutableStateFlow(CharactersState())
    val state: StateFlow<CharactersState> = _state.asStateFlow()

    init {
        repository.characters
            .onEach { entities -> _state.value = _state.value.copy(characters = entities.map(::mapToCharacter)) }
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
        }
    }

    private fun updateFilter(transform: (CharacterFilter) -> CharacterFilter) {
        _state.update { currentState -> currentState.copy(filter = transform(currentState.filter)) }
    }

    private suspend fun importFile(intent: CharactersIntent.ImportFile) {
        when (val result = TavernCardParser.parse(intent.bytes, intent.filename)) {
            is ParseResult.Success -> repository.import(result.data)
            is ParseResult.Failure -> Unit // TODO: surface error via state
        }
    }

    private fun mapToCharacter(entity: CharacterEntity): Character = Character(
        id = entity.id,
        name = entity.name,
        letter = entity.name.firstOrNull()?.uppercase() ?: "?",
        tagline = entity.description.substituteCharName(entity.name).take(TAGLINE_PREVIEW_LENGTH),
        tags = entity.tags.map { Tag(it) },
        chats = entity.chatCount,
        lastUsed = entity.lastUsedAt?.let { formatTimestamp(it) } ?: "",
        pinned = entity.pinned,
        source = entity.creator,
        avatarBytes = entity.avatarData,
    )

    private fun formatTimestamp(ms: Long): String {
        val dt = Instant.fromEpochMilliseconds(ms).toLocalDateTime(TimeZone.currentSystemDefault())
        return dt.date.toString()
    }
}
