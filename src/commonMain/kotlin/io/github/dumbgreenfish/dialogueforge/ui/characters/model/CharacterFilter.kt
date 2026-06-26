package io.github.dumbgreenfish.dialogueforge.ui.characters.model

data class CharacterFilter(
    val quick: CharacterQuickFilter = CharacterQuickFilter.All,
    val includeTags: List<Tag> = emptyList(),
    val excludeTags: List<Tag> = emptyList(),
) {
    val activeCount: Int
        get() = (if (quick != CharacterQuickFilter.All) 1 else 0) + includeTags.size + excludeTags.size
}