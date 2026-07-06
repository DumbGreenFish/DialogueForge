package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.bubble

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors

@Composable
internal fun InlineActionIcon(
    icon: ImageVector,
    onClick: (() -> Unit)?,
    visible: Boolean,
) {
    val actionsAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
        label = "actionIcon",
    )
    Icon(
        icon,
        contentDescription = null,
        modifier = Modifier
            .size(16.dp)
            .graphicsLayer { alpha = actionsAlpha }
            .clickable(enabled = visible) { onClick?.invoke() },
        tint = ForgeColors.onSurfaceFaint,
    )
}
