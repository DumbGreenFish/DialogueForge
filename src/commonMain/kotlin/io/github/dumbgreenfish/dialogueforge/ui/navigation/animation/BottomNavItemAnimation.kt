package io.github.dumbgreenfish.dialogueforge.ui.navigation.animation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import io.github.dumbgreenfish.dialogueforge.design.ForgeAnimation
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors

internal data class BottomNavItemAnimation(
    val pillColor: Color,
    val iconColor: Color,
    val labelColor: Color,
    val pillScale: Float,
)

@Composable
internal fun rememberBottomNavItemAnimation(
    isActive: Boolean,
    interactionSource: MutableInteractionSource,
): BottomNavItemAnimation {
    val isPressed by interactionSource.collectIsPressedAsState()

    val pillColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        animationSpec = tween(ForgeAnimation.DurationStateTransition),
        label = "pillColor",
    )
    val iconColor by animateColorAsState(
        targetValue = if (isActive) ForgeColors.spark else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(ForgeAnimation.DurationStateTransition),
        label = "iconColor",
    )
    val labelColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(ForgeAnimation.DurationStateTransition),
        label = "labelColor",
    )
    val pillScale by animateFloatAsState(
        targetValue = if (isPressed) ForgeAnimation.PressScaleEmphasized else 1f,
        animationSpec = spring(stiffness = ForgeAnimation.PressStiffness),
        label = "pillScale",
    )

    return BottomNavItemAnimation(pillColor, iconColor, labelColor, pillScale)
}
