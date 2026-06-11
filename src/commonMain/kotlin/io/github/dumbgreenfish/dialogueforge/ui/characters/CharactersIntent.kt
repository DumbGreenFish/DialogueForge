package io.github.dumbgreenfish.dialogueforge.ui.characters

import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharacterFilter
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharactersViewMode

sealed class CharactersIntent {
    data class SearchChanged(val query: String)              : CharactersIntent()
    data class FilterChanged(val filter: CharacterFilter)    : CharactersIntent()
    data class ViewModeChanged(val mode: CharactersViewMode) : CharactersIntent()
    data class ImportFile(val bytes: ByteArray, val filename: String) : CharactersIntent()
}
