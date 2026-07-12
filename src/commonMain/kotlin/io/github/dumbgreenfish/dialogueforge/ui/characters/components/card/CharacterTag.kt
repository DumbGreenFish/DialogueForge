package io.github.dumbgreenfish.dialogueforge.ui.characters.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors

enum class CharacterTagTone { Primary, Secondary }

internal val TagHorzPadding = 8.dp
internal val TagVertPadding = 4.dp

@Composable
internal fun CharacterTag(label: String, tone: CharacterTagTone = CharacterTagTone.Secondary) {
    val cs = MaterialTheme.colorScheme
    val bg = when (tone) {
        CharacterTagTone.Primary   -> cs.primaryContainer
        CharacterTagTone.Secondary -> ForgeColors.surfaceContainerHigh
    }
    val fg = when (tone) {
        CharacterTagTone.Primary   -> cs.onPrimaryContainer
        CharacterTagTone.Secondary -> cs.onSurfaceVariant
    }
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = fg,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .background(bg, RoundedCornerShape(4.dp))
            .padding(horizontal = TagHorzPadding, vertical = TagVertPadding),
    )
}
