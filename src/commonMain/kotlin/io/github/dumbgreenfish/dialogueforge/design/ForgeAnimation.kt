package io.github.dumbgreenfish.dialogueforge.design

import androidx.compose.animation.core.Spring

object ForgeAnimation {
    const val DurationStateTransition = 220
    const val DurationHover = 150

    const val PressStiffness = Spring.StiffnessMediumLow
    const val PressScaleEmphasized = 0.9f
    const val PressScaleSubtle = 0.97f

    fun stateLayerAlpha(isHovered: Boolean, isPressed: Boolean) = when {
        isPressed -> 0.12f
        isHovered -> 0.08f
        else      -> 0f
    }
}
