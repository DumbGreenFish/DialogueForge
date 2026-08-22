package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
import kotlin.test.Test
import kotlin.test.assertEquals

class StreamingMessagePresentationTest {
    @Test
    fun partial_assistant_message_is_the_newest_displayed_message() {
        val persisted = message("user", MessageRole.User, "Question")
        val partial = message("streaming", MessageRole.Assistant, "Visible partial")

        val displayed = messagesForDisplay(
            DialogueState(messages = listOf(persisted), streamingMessage = partial),
        )

        assertEquals(listOf(partial, persisted), displayed)
    }

    @Test
    fun completed_state_displays_only_persisted_assistant_message() {
        val completed = message("assistant", MessageRole.Assistant, "Complete")

        val displayed = messagesForDisplay(
            DialogueState(messages = listOf(completed), streamingMessage = null),
        )

        assertEquals(listOf(completed), displayed)
    }

    private fun message(id: String, role: MessageRole, text: String) = Message(
        id = id,
        role = role,
        text = text,
        timestamp = 1L,
    )
}
