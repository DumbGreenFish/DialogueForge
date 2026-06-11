package io.github.dumbgreenfish.dialogueforge.ui.characters.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character

private const val VISIBLE_TAGS_MAX  = 3
private const val TAGLINE_MAX_LINES = 2

@Composable
internal fun CharacterCardList(char: Character, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val borderColor = if (char.pinned) cs.outlineVariant else cs.outline
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
        color = cs.surface,
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CharacterAvatar(letter = char.letter, modifier = Modifier.size(60.dp), shape = ForgeShape.avatar, fontSize = 25.sp, avatarBytes = char.avatarBytes)
            ListContent(char)
        }
    }
}

@Composable
private fun RowScope.ListContent(char: Character) {
    val cs = MaterialTheme.colorScheme
    val visibleTags = char.tags.take(VISIBLE_TAGS_MAX)
    val extraCount = char.tags.size - visibleTags.size
    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(char.name, style = MaterialTheme.typography.titleSmall, color = cs.onSurface)
            if (char.pinned) {
                Spacer(Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Filled.PushPin,
                    contentDescription = null,
                    tint = ForgeColors.spark,
                    modifier = Modifier.size(15.dp).graphicsLayer { rotationZ = ForgeShape.pinRotationDeg },
                )
            }
            Spacer(Modifier.weight(1f))
            Text(char.lastUsed, style = MaterialTheme.typography.bodySmall, color = ForgeColors.onSurfaceFaint)
        }
        Text(
            text = char.tagline,
            style = MaterialTheme.typography.bodyMedium,
            color = cs.onSurfaceVariant,
            maxLines = TAGLINE_MAX_LINES,
            overflow = TextOverflow.Ellipsis,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            visibleTags.forEachIndexed { i, tag ->
                CharacterTag(label = tag, tone = if (i == 0) CharacterTagTone.Primary else CharacterTagTone.Secondary)
            }
            if (extraCount > 0) CharacterTag(label = "+$extraCount")
        }
    }
}
