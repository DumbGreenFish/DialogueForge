package io.github.dumbgreenfish.dialogueforge.data.generation

import android.app.Application
import android.app.Notification
import androidx.test.platform.app.InstrumentationRegistry
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AndroidGenerationNotifierTest {
    private val application: Application
        get() = InstrumentationRegistry.getInstrumentation()
            .targetContext
            .applicationContext as Application

    @Test
    fun opening_character_chat_removes_its_existing_completion_notifications() {
        val visibility = ConversationVisibility()
        val notificationGateway = FakeCompletionNotificationGateway()
        val notifier = AndroidGenerationNotifier(application, visibility, notificationGateway)
        val result = GenerationResult.Success(
            characterId = "character-a",
            characterName = "Airi",
            avatar = null,
            response = "Hello",
        )

        visibility.setAppVisible(false)
        notifier.completed("conversation-a", result)
        assertTrue(notificationGateway.activeNotificationIds.isNotEmpty())

        visibility.setAppVisible(true)
        visibility.showCharacter("character-a")

        assertFalse(notificationGateway.activeNotificationIds.isNotEmpty())
    }

    private class FakeCompletionNotificationGateway : CompletionNotificationGateway {
        val activeNotificationIds = mutableSetOf<Int>()

        override fun notify(notificationId: Int, notification: Notification) {
            activeNotificationIds += notificationId
        }

        override fun cancel(notificationId: Int) {
            activeNotificationIds -= notificationId
        }
    }
}
