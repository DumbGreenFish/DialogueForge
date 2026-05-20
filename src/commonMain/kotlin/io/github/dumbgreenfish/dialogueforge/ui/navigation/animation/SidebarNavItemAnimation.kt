package io.github.dumbgreenfish.dialogueforge.ui.navigation.animation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
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
    val isHovered by interactionSource.collectIsHoveredAsState()
    val colors = navAnimationColors()
    val stateAlpha = ForgeAnimation.stateLayerAlpha(isHovered, isPressed)

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isActive        -> lerp(colors.primaryContainer, colors.onPrimaryContainer, stateAlpha)
            stateAlpha > 0f -> colors.onSurface.copy(alpha = stateAlpha)
            else            -> Color.Transparent
        },
        animationSpec = tween(ForgeAnimation.DurationHover),
        label = "backgroundColor",
    )
    val iconColor by animateColorAsState(
        targetValue = when {
            isActive        -> ForgeColors.spark
            stateAlpha > 0f -> colors.onSurface
            else            -> colors.onSurfaceVariant
        },
        animationSpec = tween(ForgeAnimation.DurationHover),
        label = "iconColor",
    )
    val textColor by animateColorAsState(
        targetValue = if (isActive) colors.onPrimaryContainer else colors.onSurface,
        animationSpec = tween(ForgeAnimation.DurationHover),
        label = "textColor",
    )
    val rowScale by animateFloatAsState(
        targetValue = if (isPressed) ForgeAnimation.PressScaleSubtle else 1f,
        animationSpec = spring(stiffness = ForgeAnimation.PressStiffness),
        label = "rowScale",
    )

    return SidebarNavItemAnimation(backgroundColor, iconColor, textColor, rowScale)
}
