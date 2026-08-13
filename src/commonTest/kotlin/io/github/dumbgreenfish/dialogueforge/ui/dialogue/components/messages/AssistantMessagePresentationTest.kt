package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AssistantMessagePresentationTest {
    @Test
    fun empty_conversation_has_no_greeting_presentation() {
        assertFalse(usesGreetingPresentation(emptyList(), assistantMessage("missing")))
    }

    @Test
    fun single_user_message_has_no_greeting_presentation() {
        val message = userMessage("user")

        assertFalse(usesGreetingPresentation(listOf(message), message))
    }

    @Test
    fun single_assistant_message_uses_greeting_presentation() {
        val message = assistantMessage("greeting")

        assertTrue(usesGreetingPresentation(listOf(message), message))
        assertEquals(64.dp, assistantAvatarTargetSize(isGreeting = true))
    }

    @Test
    fun assistant_and_user_messages_use_regular_assistant_avatar() {
        val assistant = assistantMessage("greeting")
        val messages = listOf(userMessage("reply"), assistant)

        assertFalse(usesGreetingPresentation(messages, assistant))
        assertEquals(48.dp, assistantAvatarTargetSize(usesGreetingPresentation(messages, assistant)))
    }

    @Test
    fun two_assistant_messages_use_equal_regular_avatars() {
        val messages = listOf(
            assistantMessage("newest-assistant"),
            assistantMessage("greeting"),
        )

        val sizes = messages.map { message ->
            assistantAvatarTargetSize(usesGreetingPresentation(messages, message))
        }

        assertEquals(listOf(48.dp, 48.dp), sizes)
    }

    @Test
    fun screenshot_conversation_uses_equal_regular_assistant_avatars() {
        val messages = listOf(
            assistantMessage("newest-assistant"),
            userMessage("user"),
            assistantMessage("greeting"),
        )

        val assistantSizes = messages
            .filter { it.role == MessageRole.Assistant }
            .map { message ->
                assistantAvatarTargetSize(usesGreetingPresentation(messages, message))
            }

        assertEquals(listOf(48.dp, 48.dp), assistantSizes)
    }

    private fun assistantMessage(id: String) = message(id, MessageRole.Assistant)

    private fun userMessage(id: String) = message(id, MessageRole.User)

    private fun message(id: String, role: MessageRole) = Message(
        id = id,
        role = role,
        text = id,
        timestamp = 0L,
    )
}
