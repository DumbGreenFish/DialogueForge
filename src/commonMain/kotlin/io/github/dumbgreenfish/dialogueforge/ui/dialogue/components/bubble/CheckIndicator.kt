package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.bubble

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeAnimation
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors

private val CheckIndicatorSize = 24.dp
private val CheckIndicatorStroke = 2.dp
private val CheckIconSize = 14.dp

@Composable
internal fun CheckIndicator(isSelected: Boolean) {
    val cs = MaterialTheme.colorScheme
    val duration = ForgeAnimation.DurationStateTransition

    val fillAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(duration, easing = FastOutSlowInEasing),
        label = "checkFill",
    )
    val checkScale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(
            durationMillis = duration / 2,
            delayMillis = if (isSelected) duration / 3 else 0,
            easing = FastOutSlowInEasing,
        ),
        label = "checkScale",
    )

    Box(
        modifier = Modifier
            .size(CheckIndicatorSize)
            .background(
                color = ForgeColors.spark.copy(alpha = fillAlpha),
                shape = CircleShape,
            )
            .border(
                width = CheckIndicatorStroke,
                color = ForgeColors.spark.copy(alpha = if (isSelected) 0f else 1f),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            Icon(
                Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier
                    .size(CheckIconSize)
                    .graphicsLayer(
                        scaleX = checkScale,
                        scaleY = checkScale,
                    ),
                tint = cs.onTertiary,
            )
        }
    }
}
