package io.github.dumbgreenfish.dialogueforge.notification

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import androidx.test.platform.app.InstrumentationRegistry
import io.github.dumbgreenfish.dialogueforge.R
import io.github.dumbgreenfish.dialogueforge.generation.GenerationForegroundService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GenerationNotificationFactoryTest {
    private val context: Context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    @Suppress("DEPRECATION")
    fun foreground_service_is_private_and_declared_as_data_sync() {
        val component = ComponentName(context, GenerationForegroundService::class.java)
        val info = context.packageManager.getServiceInfo(component, 0)

        assertFalseExported(info.exported)
        assertTrue(info.foregroundServiceType and ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC != 0)

        val requested = context.packageManager
            .getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
            .requestedPermissions
            .orEmpty()
            .toSet()
        assertTrue("android.permission.FOREGROUND_SERVICE" in requested)
        assertTrue("android.permission.FOREGROUND_SERVICE_DATA_SYNC" in requested)
        assertTrue("android.permission.POST_NOTIFICATIONS" in requested)
    }

    @Test
    fun completion_notification_uses_character_as_messaging_person_without_large_icon() {
        val avatar = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888)
        val notification = GenerationNotificationFactory(context).completion(
            notificationId = 41,
            characterId = "character-a",
            characterName = "Airi",
            response = "First line\nSecond line",
            avatar = avatar,
        )

        assertEquals(Notification.VISIBILITY_PUBLIC, notification.visibility)
        val style = Notification.Builder.recoverBuilder(context, notification).style
        val message = assertNotNull(style as? Notification.MessagingStyle).messages.single()
        assertEquals("First line\nSecond line", message.text)
        assertEquals("Airi", message.senderPerson?.name)
        assertNotNull(message.senderPerson?.icon)
        assertEquals(android.graphics.drawable.Icon.TYPE_BITMAP, message.senderPerson?.icon?.type)
        assertNull(notification.getLargeIcon())
        assertNotNull(notification.contentIntent)
        assertTrue(notification.flags and Notification.FLAG_AUTO_CANCEL != 0)
    }

    @Test
    fun ongoing_notification_is_ongoing_and_reports_parallel_work_count() {
        val notification = GenerationNotificationFactory(context).ongoing(activeCount = 3)

        assertTrue(notification.flags and Notification.FLAG_ONGOING_EVENT != 0)
        assertEquals(3, notification.number)
        assertNotNull(notification.actions.firstOrNull())
    }

    @Test
    fun single_ongoing_notification_names_the_character_and_is_public() {
        val notification = GenerationNotificationFactory(context).ongoing(
            activeCount = 1,
            characterName = "Airi",
        )

        assertEquals(Notification.VISIBILITY_PUBLIC, notification.visibility)
        assertEquals(
            context.getString(R.string.notification_generation_single, "Airi"),
            notification.extras.getCharSequence(Notification.EXTRA_TEXT),
        )
        assertEquals(
            context.getString(R.string.notification_generation_single, "Airi"),
            notification.extras.getCharSequence(Notification.EXTRA_BIG_TEXT),
        )
    }

    private fun assertFalseExported(exported: Boolean) {
        assertEquals(false, exported)
    }
}
