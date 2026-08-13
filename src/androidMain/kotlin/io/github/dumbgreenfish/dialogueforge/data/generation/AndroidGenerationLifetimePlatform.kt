package io.github.dumbgreenfish.dialogueforge.data.generation

import android.app.Application
import android.app.NotificationManager
import android.content.Intent
import io.github.dumbgreenfish.dialogueforge.generation.GenerationForegroundService
import io.github.dumbgreenfish.dialogueforge.notification.GenerationNotificationFactory
import org.koin.core.annotation.Single

internal interface AndroidGenerationLifetimePlatform {
    fun startForegroundService(characterName: String?)

    fun updateNotification(activeCount: Int, characterName: String?)

}

@Single(binds = [AndroidGenerationLifetimePlatform::class])
internal class DefaultAndroidGenerationLifetimePlatform(
    private val application: Application,
) : AndroidGenerationLifetimePlatform {
    private val notifications = GenerationNotificationFactory(application)
    private val notificationManager = application.getSystemService(NotificationManager::class.java)

    override fun startForegroundService(characterName: String?) {
        application.startForegroundService(
            Intent(application, GenerationForegroundService::class.java).apply {
                action = GenerationForegroundService.ACTION_START
                characterName?.let {
                    putExtra(GenerationForegroundService.EXTRA_CHARACTER_NAME, it)
                }
            },
        )
    }

    override fun updateNotification(activeCount: Int, characterName: String?) {
        notificationManager.notify(
            GenerationNotificationFactory.ONGOING_NOTIFICATION_ID,
            notifications.ongoing(activeCount, characterName),
        )
    }
}
