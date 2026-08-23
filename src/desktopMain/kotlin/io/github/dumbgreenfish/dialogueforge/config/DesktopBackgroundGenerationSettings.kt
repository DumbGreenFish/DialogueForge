package io.github.dumbgreenfish.dialogueforge.config

import org.koin.core.annotation.Single

@Single(binds = [BackgroundGenerationSettings::class])
class DesktopBackgroundGenerationSettings : BackgroundGenerationSettings {
    override val isAvailable: Boolean = false
    override fun shouldShowOnboarding(): Boolean = false
    override fun completeOnboarding() = Unit
    override fun requestNotificationPermission() = Unit
    override fun configureNotifications() = Unit
    override fun openBackgroundSettings() = Unit
}
