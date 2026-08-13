package io.github.dumbgreenfish.dialogueforge.data.generation

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import io.github.dumbgreenfish.dialogueforge.notification.AndroidNotificationPermission
import org.koin.core.annotation.Single

@Single(binds = [BackgroundGenerationSettings::class])
class AndroidBackgroundGenerationSettings(
    private val application: Application,
    private val notificationPermission: AndroidNotificationPermission,
) : BackgroundGenerationSettings {
    override val isAvailable: Boolean = true

    private val preferences by lazy {
        application.getSharedPreferences(PREFERENCES_NAME, Application.MODE_PRIVATE)
    }

    override fun shouldShowOnboarding(): Boolean =
        !preferences.getBoolean(KEY_ONBOARDING_COMPLETE, false)

    override fun completeOnboarding() {
        preferences.edit().putBoolean(KEY_ONBOARDING_COMPLETE, true).apply()
    }

    override fun requestNotificationPermission() = notificationPermission.requestOnce()

    override fun configureNotifications() = notificationPermission.requestOrOpenSettings()

    override fun openBackgroundSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", application.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (intent.resolveActivity(application.packageManager) != null) {
            application.startActivity(intent)
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "background_generation"
        const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    }
}
