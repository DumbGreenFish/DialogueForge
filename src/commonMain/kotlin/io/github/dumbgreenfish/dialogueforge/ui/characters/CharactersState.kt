package io.github.dumbgreenfish.dialogueforge.ui.characters

import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharacterFilter
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharacterQuickFilter
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharactersViewMode
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Tag

data class CharactersState(
    val characters: List<Character> = emptyList(),
    val query: String = "",
    val filter: CharacterFilter = CharacterFilter(),
    val viewMode: CharactersViewMode = CharactersViewMode.List,
    val error: String? = null,
) {
    val availableTags: List<Tag>
        get() = characters.flatMap { it.tags }.distinct().sortedBy { it.value }

    val displayed: List<Character>
        get() {
            var r = characters
            when (filter.quick) {
                CharacterQuickFilter.All    -> Unit
                CharacterQuickFilter.Pinned -> r = r.filter { it.pinned }
                // TODO(origin): not yet implemented
                CharacterQuickFilter.Made, CharacterQuickFilter.Imported -> Unit
            }
            if (filter.includeTags.isNotEmpty()) {
                r = r.filter { c -> filter.includeTags.all { it in c.tags } }
            }
            if (filter.excludeTags.isNotEmpty()) {
                r = r.filter { c -> filter.excludeTags.none { it in c.tags } }
            }
            if (query.isNotBlank()) {
                val q = query.trim().lowercase()
                r = r.filter {
                    q in it.name.lowercase() || q in it.tagline.lowercase() || it.tags.any { t -> q in t.value }
                }
            }
            return r.sortedByDescending { it.pinned }
        }
}