package io.github.dumbgreenfish.dialogueforge.ui.characters

import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharacterQuickFilter
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharactersViewMode
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Tag

sealed class CharactersIntent {
    data class SearchChanged(val query: String): CharactersIntent()
    data class ViewModeChanged(val mode: CharactersViewMode) : CharactersIntent()
    data class QuickFilterChanged(val quick: CharacterQuickFilter) : CharactersIntent()
    data class IncludeTagAdded(val tag: Tag)   : CharactersIntent()
    data class IncludeTagRemoved(val tag: Tag) : CharactersIntent()
    data class ExcludeTagAdded(val tag: Tag)   : CharactersIntent()
    data class ExcludeTagRemoved(val tag: Tag) : CharactersIntent()
    data object FiltersReset                   : CharactersIntent()
    data class DeleteCharacter(val id: String) : CharactersIntent()
    data object DismissError : CharactersIntent()
    data class ImportFile(val bytes: ByteArray, val filename: String) : CharactersIntent()
}