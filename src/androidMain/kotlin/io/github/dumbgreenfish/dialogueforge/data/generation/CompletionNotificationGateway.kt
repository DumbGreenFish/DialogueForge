package io.github.dumbgreenfish.dialogueforge.data.generation

import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import org.koin.core.annotation.Single

internal interface CompletionNotificationGateway {
    fun notify(notificationId: Int, notification: Notification)

    fun cancel(notificationId: Int)
}

@Single(binds = [CompletionNotificationGateway::class])
internal class AndroidCompletionNotificationGateway(
    application: Application,
) : CompletionNotificationGateway {
    private val notificationManager = application.getSystemService(NotificationManager::class.java)

    override fun notify(notificationId: Int, notification: Notification) {
        notificationManager.notify(notificationId, notification)
    }

    override fun cancel(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
}
