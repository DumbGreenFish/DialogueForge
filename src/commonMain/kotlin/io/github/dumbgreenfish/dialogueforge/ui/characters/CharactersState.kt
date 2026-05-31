package io.github.dumbgreenfish.dialogueforge.ui.characters

import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharacterFilter
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharactersViewMode

data class CharactersState(
    val characters: List<Character> = emptyList(),
    val query: String = "",
    val activeFilter: CharacterFilter = CharacterFilter.All,
    val viewMode: CharactersViewMode = CharactersViewMode.List,
) {
    val availableTags: List<String>
        get() = characters.flatMap { it.tags }.distinct().sorted()

    val displayed: List<Character>
        get() {
            var r = characters
            when (val f = activeFilter) {
                CharacterFilter.All    -> Unit
                CharacterFilter.Pinned -> r = r.filter { it.pinned }
                is CharacterFilter.Tag -> r = r.filter { f.tag in it.tags }
            }
            if (query.isNotBlank()) {
                val q = query.trim().lowercase()
                r = r.filter {
                    q in it.name.lowercase() ||
                    q in it.tagline.lowercase() ||
                    it.tags.any { t -> q in t }
                }
            }
            return r.sortedByDescending { it.pinned }
        }
}
