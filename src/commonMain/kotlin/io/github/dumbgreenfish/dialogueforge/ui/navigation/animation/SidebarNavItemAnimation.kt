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

internal data class SidebarNavItemAnimation(
    val backgroundColor: Color,
    val iconColor: Color,
    val textColor: Color,
    val rowScale: Float,
)

@Composable
internal fun rememberSidebarNavItemAnimation(
    isActive: Boolean,
    interactionSource: MutableInteractionSource,
): SidebarNavItemAnimation {
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        animationSpec = tween(ForgeAnimation.DurationStateTransition),
        label = "backgroundColor",
    )
    val iconColor by animateColorAsState(
        targetValue = if (isActive) ForgeColors.spark else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(ForgeAnimation.DurationStateTransition),
        label = "iconColor",
    )
    val textColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(ForgeAnimation.DurationStateTransition),
        label = "textColor",
    )
    val rowScale by animateFloatAsState(
        targetValue = if (isPressed) ForgeAnimation.PressScaleSubtle else 1f,
        animationSpec = spring(stiffness = ForgeAnimation.PressStiffness),
        label = "rowScale",
    )

    return SidebarNavItemAnimation(backgroundColor, iconColor, textColor, rowScale)
}
