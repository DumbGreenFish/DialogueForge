package io.github.dumbgreenfish.dialogueforge.ui.characters.model

sealed class CharacterFilter {
    data object All    : CharacterFilter()
    data object Pinned : CharacterFilter()
    data class  Tag(val tag: String) : CharacterFilter()
}
