package io.github.dumbgreenfish.dialogueforge.design

import androidx.compose.animation.core.Spring
import androidx.compose.runtime.mutableIntStateOf

object ForgeAnimation {
    private const val BASE_DURATION_TRANSITION = 220
    private const val BASE_DURATION_HOVER = 150

    private val _durationStateTransition = mutableIntStateOf(BASE_DURATION_TRANSITION)
    val DurationStateTransition: Int get() = _durationStateTransition.intValue

    private val _durationHover = mutableIntStateOf(BASE_DURATION_HOVER)
    val DurationHover: Int get() = _durationHover.intValue

    const val PressStiffness = Spring.StiffnessMediumLow
    const val PressScaleEmphasized = 0.9f
    const val PressScaleSubtle = 0.97f

    const val PredictiveBackMinScale = 0.9f
    const val PredictiveBackMinAlpha = 0.85f

    fun setSpeedMultiplier(multiplier: Float) {
        _durationStateTransition.intValue =
            if (multiplier == 0f) 0 else (BASE_DURATION_TRANSITION * multiplier).toInt().coerceAtLeast(1)
        _durationHover.intValue =
            if (multiplier == 0f) 0 else (BASE_DURATION_HOVER * multiplier).toInt().coerceAtLeast(1)
    }

    fun stateLayerAlpha(isHovered: Boolean, isPressed: Boolean) = when {
        isPressed -> 0.12f
        isHovered -> 0.08f
        else      -> 0f
    }
}
