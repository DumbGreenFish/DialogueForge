package io.github.dumbgreenfish.dialogueforge.design

import androidx.compose.animation.core.Spring

object ForgeAnimation {
    private const val BASE_DURATION_TRANSITION = 220
    private const val BASE_DURATION_HOVER = 150

    var DurationStateTransition: Int = BASE_DURATION_TRANSITION
        private set
    var DurationHover: Int = BASE_DURATION_HOVER
        private set

    const val PressStiffness = Spring.StiffnessMediumLow
    const val PressScaleEmphasized = 0.9f
    const val PressScaleSubtle = 0.97f

    fun setSpeedMultiplier(multiplier: Float) {
        DurationStateTransition = if (multiplier == 0f) 0 else (BASE_DURATION_TRANSITION * multiplier).toInt().coerceAtLeast(1)
        DurationHover = if (multiplier == 0f) 0 else (BASE_DURATION_HOVER * multiplier).toInt().coerceAtLeast(1)
    }

    fun stateLayerAlpha(isHovered: Boolean, isPressed: Boolean) = when {
        isPressed -> 0.12f
        isHovered -> 0.08f
        else      -> 0f
    }
}
