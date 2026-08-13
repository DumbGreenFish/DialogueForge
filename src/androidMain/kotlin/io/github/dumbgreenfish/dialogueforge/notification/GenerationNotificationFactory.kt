package io.github.dumbgreenfish.dialogueforge.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Person
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.os.Build
import io.github.dumbgreenfish.dialogueforge.MainActivity
import io.github.dumbgreenfish.dialogueforge.R
import io.github.dumbgreenfish.dialogueforge.generation.GenerationForegroundService

class GenerationNotificationFactory(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    init {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                ONGOING_CHANNEL_ID,
                context.getString(R.string.notification_generation_channel),
                NotificationManager.IMPORTANCE_LOW,
            ),
        )
        notificationManager.createNotificationChannel(
            NotificationChannel(
                COMPLETION_CHANNEL_ID,
                context.getString(R.string.notification_completion_channel),
                NotificationManager.IMPORTANCE_DEFAULT,
            ),
        )
    }

    fun ongoing(activeCount: Int, characterName: String? = null): Notification {
        val title = if (activeCount == 1) {
            context.getString(R.string.notification_generation_title)
        } else {
            context.getString(R.string.notification_generation_title_multiple)
        }
        val openApp = PendingIntent.getActivity(
            context,
            ONGOING_NOTIFICATION_ID,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val stopAll = PendingIntent.getService(
            context,
            ONGOING_NOTIFICATION_ID,
            Intent(context, GenerationForegroundService::class.java).apply {
                action = GenerationForegroundService.ACTION_STOP_ALL
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val stopAction = Notification.Action.Builder(
            R.drawable.ic_notification,
            context.getString(R.string.notification_stop_all),
            stopAll,
        ).build()
        val contentText = when {
            activeCount == 1 && characterName != null ->
                context.getString(R.string.notification_generation_single, characterName)
            activeCount > 1 -> context.getString(R.string.notification_generation_multiple, activeCount)
            else -> null
        }

        return Notification.Builder(context, ONGOING_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .apply {
                contentText?.let {
                    setContentText(it)
                    setStyle(Notification.BigTextStyle().bigText(it))
                }
            }
            .setContentIntent(openApp)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setNumber(activeCount)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .addAction(stopAction)
            .build()
    }

    fun completion(
        notificationId: Int,
        characterId: String,
        characterName: String,
        response: String,
        avatar: Bitmap?,
    ): Notification {
        val openChatIntent = openChatIntent(characterId)
        val openChat = PendingIntent.getActivity(
            context,
            notificationId,
            openChatIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val character = Person.Builder()
            .setName(characterName)
            .setKey(characterId)
            .apply { avatar?.let { setIcon(Icon.createWithBitmap(it)) } }
            .build()
        val user = Person.Builder()
            .setName(context.applicationInfo.loadLabel(context.packageManager))
            .build()
        val style = Notification.MessagingStyle(user)
            .addMessage(response, System.currentTimeMillis(), character)
            .setGroupConversation(false)
        val shortcutId = publishConversationShortcut(
            characterId = characterId,
            characterName = characterName,
            avatar = avatar,
            character = character,
            intent = openChatIntent,
        )

        return Notification.Builder(context, COMPLETION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setStyle(style)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .apply { shortcutId?.let(::setShortcutId) }
            .setContentIntent(openChat)
            .setAutoCancel(true)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .build()
    }

    private fun openChatIntent(characterId: String) = Intent(context, MainActivity::class.java).apply {
        action = "$OPEN_CHAT_ACTION_PREFIX$characterId"
        putExtra(MainActivity.EXTRA_CHARACTER_ID, characterId)
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }

    private fun publishConversationShortcut(
        characterId: String,
        characterName: String,
        avatar: Bitmap?,
        character: Person,
        intent: Intent,
    ): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return null
        val shortcutId = "$CONVERSATION_SHORTCUT_PREFIX$characterId"
        val shortcut = ShortcutInfo.Builder(context, shortcutId)
            .setShortLabel(characterName)
            .setLongLived(true)
            .setPerson(character)
            .setIntent(intent)
            .apply { avatar?.let { setIcon(Icon.createWithBitmap(it)) } }
            .build()
        val shortcuts = context.getSystemService(ShortcutManager::class.java)

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                shortcuts.pushDynamicShortcut(shortcut)
            } else {
                val existing = shortcuts.dynamicShortcuts
                if (existing.any { it.id == shortcutId }) {
                    shortcuts.updateShortcuts(listOf(shortcut))
                } else {
                    if (existing.size >= shortcuts.maxShortcutCountPerActivity) {
                        existing.minByOrNull(ShortcutInfo::getLastChangedTimestamp)?.let {
                            shortcuts.removeDynamicShortcuts(listOf(it.id))
                        }
                    }
                    shortcuts.addDynamicShortcuts(listOf(shortcut))
                }
            }
            shortcutId
        } catch (_: IllegalArgumentException) {
            null
        } catch (_: IllegalStateException) {
            null
        }
    }

    companion object {
        const val ONGOING_NOTIFICATION_ID = 1_001
        private const val ONGOING_CHANNEL_ID = "generation_progress"
        private const val COMPLETION_CHANNEL_ID = "generation_complete"
        private const val OPEN_CHAT_ACTION_PREFIX = "io.github.dumbgreenfish.dialogueforge.OPEN_CHAT."
        private const val CONVERSATION_SHORTCUT_PREFIX = "character."
    }
}
