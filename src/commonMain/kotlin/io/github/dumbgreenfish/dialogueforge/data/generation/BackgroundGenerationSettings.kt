package io.github.dumbgreenfish.dialogueforge.data.generation

interface BackgroundGenerationSettings {
    val isAvailable: Boolean

    fun shouldShowOnboarding(): Boolean

    fun completeOnboarding()

    fun requestNotificationPermission()

    fun configureNotifications()

    fun openBackgroundSettings()
}
