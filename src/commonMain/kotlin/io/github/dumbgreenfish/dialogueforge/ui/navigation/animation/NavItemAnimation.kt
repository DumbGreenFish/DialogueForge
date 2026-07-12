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

internal data class NavItemAnimation(
    val backgroundColor: Color,
    val iconColor: Color,
    val labelColor: Color,
    val scale: Float,
)

@Composable
internal fun rememberNavItemAnimation(
    isActive: Boolean,
    interactionSource: MutableInteractionSource,
    pressScale: Float,
    labelColorFor: (isActive: Boolean, stateAlpha: Float, colors: NavAnimationColors) -> Color,
): NavItemAnimation {
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
        label = "navItemBackground",
    )
    val iconColor by animateColorAsState(
        targetValue = when {
            isActive        -> ForgeColors.spark
            stateAlpha > 0f -> colors.onSurface
            else            -> colors.onSurfaceVariant
        },
        animationSpec = tween(ForgeAnimation.DurationHover),
        label = "navItemIcon",
    )
    val labelColor by animateColorAsState(
        targetValue = labelColorFor(isActive, stateAlpha, colors),
        animationSpec = tween(ForgeAnimation.DurationHover),
        label = "navItemLabel",
    )
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressScale else 1f,
        animationSpec = spring(stiffness = ForgeAnimation.PressStiffness),
        label = "navItemScale",
    )

    return NavItemAnimation(backgroundColor, iconColor, labelColor, scale)
}
