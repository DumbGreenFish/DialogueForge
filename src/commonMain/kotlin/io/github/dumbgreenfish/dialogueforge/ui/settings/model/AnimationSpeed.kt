package io.github.dumbgreenfish.dialogueforge.ui.settings.model

enum class AnimationSpeed(val durationMultiplier: Float) {
    Fast(0.5f),
    Normal(1.0f),
    Slow(2.0f),
    Off(0f),
}
