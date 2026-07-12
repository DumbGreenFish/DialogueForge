package io.github.dumbgreenfish.dialogueforge.ui.characters.components.card

import androidx.compose.runtime.Composable
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Tag

@Composable
internal fun CardTagChips(visibleTags: List<Tag>, extraCount: Int) {
    visibleTags.forEachIndexed { index, tag ->
        CharacterTag(
            label = tag.value,
            tone = if (index == 0) CharacterTagTone.Primary else CharacterTagTone.Secondary,
        )
    }
    if (extraCount > 0) CharacterTag(label = "+$extraCount")
}
