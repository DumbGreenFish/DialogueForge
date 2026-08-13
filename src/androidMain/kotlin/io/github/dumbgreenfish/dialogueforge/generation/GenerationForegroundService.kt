package io.github.dumbgreenfish.dialogueforge.generation

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import io.github.dumbgreenfish.dialogueforge.data.generation.GenerationController
import io.github.dumbgreenfish.dialogueforge.notification.GenerationNotificationFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class GenerationForegroundService : Service() {
    private val generationController: GenerationController by lazy { GlobalContext.get().get() }
    private val notifications by lazy { GenerationNotificationFactory(this) }
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var activeGenerationsMonitor: Job? = null
    private var latestStartId = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        latestStartId = startId
        val notification = notifications.ongoing(
            generationController.activeConversationIds.value.size.coerceAtLeast(1),
            intent?.getStringExtra(EXTRA_CHARACTER_NAME),
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                GenerationNotificationFactory.ONGOING_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
            )
        } else {
            startForeground(GenerationNotificationFactory.ONGOING_NOTIFICATION_ID, notification)
        }
        if (activeGenerationsMonitor == null) {
            activeGenerationsMonitor = serviceScope.launch {
                generationController.activeConversationIds.collectLatest { activeConversationIds ->
                    if (activeConversationIds.isEmpty() && stopSelfResult(latestStartId)) {
                        stopForeground(STOP_FOREGROUND_REMOVE)
                    }
                }
            }
        }
        if (intent?.action == ACTION_STOP_ALL) generationController.cancelAll()
        return START_NOT_STICKY
    }

    override fun onTimeout(startId: Int, fgsType: Int) {
        generationController.interruptAll()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf(startId)
    }

    override fun onDestroy() {
        serviceScope.cancel()
        if (generationController.activeConversationIds.value.isNotEmpty()) {
            generationController.interruptAll()
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START = "io.github.dumbgreenfish.dialogueforge.generation.START"
        const val ACTION_STOP_ALL = "io.github.dumbgreenfish.dialogueforge.generation.STOP_ALL"
        const val EXTRA_CHARACTER_NAME = "generation_character_name"
    }
}
