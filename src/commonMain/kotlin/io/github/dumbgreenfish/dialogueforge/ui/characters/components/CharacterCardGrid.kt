package io.github.dumbgreenfish.dialogueforge.ui.characters.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character

private const val VISIBLE_TAGS_MAX  = 2
private const val NAME_MAX_LINES    = 1
private const val TAGLINE_MAX_LINES = 2
private const val GRADIENT_ALPHA    = 0.85f
private const val BADGE_ALPHA       = 0.70f

@Composable
internal fun CharacterCardGrid(char: Character, onClick: () -> Unit) {
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
        Column {
            GridSquare(char)
            GridInfo(char)
        }
    }
}

@Composable
private fun GridSquare(char: Character) {
    val bg = MaterialTheme.colorScheme.background
    val visibleTags = char.tags.take(VISIBLE_TAGS_MAX)
    val extraCount = char.tags.size - visibleTags.size
    Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
        CharacterAvatar(letter = char.letter, modifier = Modifier.fillMaxSize(), shape = ForgeShape.avatar, fontSize = 56.sp)
        GradientOverlay(bg.copy(alpha = 0f), bg.copy(alpha = GRADIENT_ALPHA)) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                visibleTags.forEachIndexed { i, tag ->
                    CharacterTag(label = tag, tone = if (i == 0) CharacterTagTone.Primary else CharacterTagTone.Secondary)
                }
                if (extraCount > 0) CharacterTag(label = "+$extraCount")
            }
        }
        if (char.pinned) PinBadge(bgAlpha = BADGE_ALPHA)
    }
}

@Composable
private fun BoxScope.GradientOverlay(
    colorTop: Color,
    colorBottom: Color,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomStart)
            .background(Brush.verticalGradient(0f to colorTop, 1f to colorBottom))
            .padding(start = 10.dp, end = 10.dp, top = 24.dp, bottom = 10.dp),
    ) { content() }
}

@Composable
private fun BoxScope.PinBadge(bgAlpha: Float) {
    val bg = MaterialTheme.colorScheme.background
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(24.dp)
            .background(bg.copy(alpha = bgAlpha), CircleShape)
            .align(Alignment.TopEnd),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.PushPin,
            contentDescription = null,
            tint = ForgeColors.spark,
            modifier = Modifier.size(13.dp).graphicsLayer { rotationZ = ForgeShape.pinRotationDeg },
        )
    }
}

@Composable
private fun GridInfo(char: Character) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = char.name,
                style = MaterialTheme.typography.titleSmall,
                color = cs.onSurface,
                maxLines = NAME_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.size(6.dp))
            Text(text = char.lastUsed, style = MaterialTheme.typography.labelSmall, color = ForgeColors.onSurfaceFaint)
        }
        Text(
            text = char.tagline,
            style = MaterialTheme.typography.bodySmall,
            color = cs.onSurfaceVariant,
            maxLines = TAGLINE_MAX_LINES,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
