package io.github.dumbgreenfish.dialogueforge.ui.characters.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors

enum class CharacterTagTone { Primary, Secondary, Tertiary }

@Composable
internal fun CharacterTag(label: String, tone: CharacterTagTone = CharacterTagTone.Secondary) {
    val cs = MaterialTheme.colorScheme
    val bg = when (tone) {
        CharacterTagTone.Primary   -> cs.primaryContainer
        CharacterTagTone.Secondary -> ForgeColors.surfaceContainerHigh
        CharacterTagTone.Tertiary  -> cs.tertiaryContainer
    }
    val fg = when (tone) {
        CharacterTagTone.Primary   -> cs.onPrimaryContainer
        CharacterTagTone.Secondary -> cs.onSurfaceVariant
        CharacterTagTone.Tertiary  -> cs.onTertiaryContainer
    }
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = fg,
        modifier = Modifier
            .background(bg, RoundedCornerShape(4.dp))
            .padding(horizontal = 9.dp, vertical = 3.dp),
    )
}
