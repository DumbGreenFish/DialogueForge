package io.github.dumbgreenfish.dialogueforge.config

interface BackgroundGenerationSettings {
    val isAvailable: Boolean

    fun shouldShowOnboarding(): Boolean

    fun completeOnboarding()

    fun requestNotificationPermission()

    fun configureNotifications()

    fun openBackgroundSettings()
}