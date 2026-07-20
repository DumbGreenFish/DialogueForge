package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
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

private val IndicatorSize = 22.dp
private val IndicatorStroke = 2.dp
private val CheckIconSize = 14.dp

@Composable
internal fun CheckIndicator(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
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
        modifier = modifier
            .size(IndicatorSize)
            .background(
                color = ForgeColors.spark.copy(alpha = fillAlpha),
                shape = CircleShape,
            )
            .border(
                width = IndicatorStroke,
                color = if (isSelected) ForgeColors.spark.copy(alpha = 0f) else ForgeColors.spark,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally(),
        ) {
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
