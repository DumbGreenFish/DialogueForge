package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
import kotlin.test.Test
import kotlin.test.assertEquals

class StreamingMessageDuplicateSuppressionTest {
    @Test
    fun current_persisted_stream_copy_is_suppressed_without_removing_older_identical_answer() {
        val streaming = message("streaming-conversation", MessageRole.Assistant, "Same text", 0L)
        val currentPersisted = message("current", MessageRole.Assistant, "Same text", 3L)
        val user = message("user", MessageRole.User, "Question", 2L)
        val olderIdentical = message("older", MessageRole.Assistant, "Same text", 1L)

        val displayed = messagesForDisplay(
            DialogueState(
                messages = listOf(currentPersisted, user, olderIdentical),
                streamingMessage = streaming,
            ),
        )

        assertEquals(listOf(streaming, user, olderIdentical), displayed)
    }

    @Test
    fun active_persisted_assistant_is_presented_as_streaming_without_changing_identity() {
        val persisted = message("assistant-id", MessageRole.Assistant, "Partial", 42L)
        val user = message("user-id", MessageRole.User, "Question", 41L)

        val displayed = messagesForDisplay(
            DialogueState(
                isGenerating = true,
                messages = listOf(persisted, user),
            ),
        )

        assertEquals("assistant-id", displayed.first().id)
        assertEquals("Partial", displayed.first().text)
        assertEquals(0L, displayed.first().timestamp)
        assertEquals(user, displayed.last())
    }

    @Test
    fun active_latest_user_message_is_not_presented_as_streaming() {
        val user = message("user-id", MessageRole.User, "Question", 42L)
        val assistant = message("assistant-id", MessageRole.Assistant, "Older answer", 41L)
        val messages = listOf(user, assistant)

        val displayed = messagesForDisplay(
            DialogueState(
                isGenerating = true,
                messages = messages,
            ),
        )

        assertEquals(messages, displayed)
    }

    private fun message(
        id: String,
        role: MessageRole,
        text: String,
        timestamp: Long,
    ) = Message(
        id = id,
        role = role,
        text = text,
        timestamp = timestamp,
    )
}
