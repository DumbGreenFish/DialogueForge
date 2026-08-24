package io.github.dumbgreenfish.dialogueforge.service.generation.execution

import io.github.dumbgreenfish.dialogueforge.data.model.ConversationEntity
import io.github.dumbgreenfish.dialogueforge.data.model.MessageEntity
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.ConversationResult
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.DialogueRepository
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatError
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatErrorType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ConversationGenerationSessionTest {
    @Test
    fun begin_prepares_conversation_and_reads_one_shot_history_without_resorting() = runBlocking {
        val repository = RecordingDialogueRepository(
            initialMessages = listOf(
                message("assistant", "assistant", "Existing answer", 8),
                message("user", "user", "Existing question", 3),
            ),
        )

        val history = session(repository).begin("New question")

        assertFalse(repository.observedMessages)
        assertTrue(repository.readHistorySnapshot)
        assertEquals(
            listOf("clear-error", "add:user:New question", "set-error:Interrupted:", "history"),
            repository.operations,
        )
        assertEquals(
            listOf(
                "assistant" to "Existing answer",
                "user" to "Existing question",
                "user" to "New question",
            ),
            history,
        )
    }

    @Test
    fun begin_without_user_text_does_not_create_user_message() = runBlocking {
        val repository = RecordingDialogueRepository(
            initialMessages = listOf(message("existing", "user", "Existing question", 0)),
        )

        val history = session(repository).begin(null)

        assertEquals(listOf("clear-error", "set-error:Interrupted:", "history"), repository.operations)
        assertEquals(listOf("user" to "Existing question"), history)
    }

    @Test
    fun streaming_updates_create_one_assistant_message_then_update_the_same_record() = runBlocking {
        val repository = RecordingDialogueRepository()
        val session = session(repository)

        session.writeAssistantResponse("Part")

        assertEquals(1, repository.messages.size)
        val createdId = repository.messages.single().id
        assertEquals(listOf("Part"), repository.messages.map(MessageEntity::text))
        assertEquals(listOf("add:assistant:Part"), repository.operations)

        session.writeAssistantResponse("Partial answer")

        assertEquals(1, repository.messages.size)
        assertEquals(createdId, repository.messages.single().id)
        assertEquals("Partial answer", repository.messages.single().text)
        assertEquals(
            listOf("add:assistant:Part", "update:$createdId:Partial answer"),
            repository.operations,
        )
    }

    @Test
    fun buffered_completion_creates_assistant_message_once_and_clears_marker() = runBlocking {
        val repository = RecordingDialogueRepository()

        session(repository).complete("Buffered answer")

        assertEquals(listOf("Buffered answer"), repository.messages.map(MessageEntity::text))
        assertEquals(listOf("add:assistant:Buffered answer", "clear-error"), repository.operations)
        assertNull(repository.errorType)
    }

    @Test
    fun streaming_completion_reuses_existing_assistant_message_without_duplicate() = runBlocking {
        val repository = RecordingDialogueRepository()
        val session = session(repository)
        session.writeAssistantResponse("Almost complete")
        assertEquals(1, repository.messages.size)
        val createdId = repository.messages.single().id
        repository.operations.clear()

        session.complete("Complete answer")

        assertEquals(1, repository.messages.size)
        assertEquals(createdId, repository.messages.single().id)
        assertEquals("Complete answer", repository.messages.single().text)
        assertEquals(listOf("update:$createdId:Complete answer", "clear-error"), repository.operations)
    }

    @Test
    fun failure_keeps_streamed_message_and_persists_mapped_error() = runBlocking {
        val repository = RecordingDialogueRepository()
        val session = session(repository)
        session.writeAssistantResponse("Saved partial")
        assertEquals(1, repository.messages.size)
        repository.operations.clear()

        session.fail(ChatError(ChatErrorType.Server, "Provider unavailable"))

        assertEquals("Saved partial", repository.messages.single().text)
        assertEquals(ChatErrorType.Server.name, repository.errorType)
        assertEquals("Provider unavailable", repository.errorText)
        assertEquals(listOf("set-error:Server:Provider unavailable"), repository.operations)
    }

    @Test
    fun user_cancellation_keeps_streamed_message_and_clears_marker() = runBlocking {
        val repository = RecordingDialogueRepository()
        val session = session(repository)
        session.writeAssistantResponse("Saved partial")
        assertEquals(1, repository.messages.size)
        repository.errorType = ChatErrorType.Interrupted.name
        repository.operations.clear()

        session.cancel()

        assertEquals("Saved partial", repository.messages.single().text)
        assertNull(repository.errorType)
        assertEquals(listOf("clear-error"), repository.operations)
    }

    private fun session(repository: DialogueRepository) =
        ConversationGenerationSession(CONVERSATION_ID, repository)

    private class RecordingDialogueRepository(
        initialMessages: List<MessageEntity> = emptyList(),
    ) : DialogueRepository {
        val messages = initialMessages.toMutableList()
        val operations = mutableListOf<String>()
        var observedMessages = false
        var readHistorySnapshot = false
        var errorType: String? = null
        var errorText: String = ""
        private var nextMessageId = initialMessages.size

        override fun getMessages(conversationId: String): Flow<List<MessageEntity>> {
            observedMessages = true
            return flowOf(messages.toList())
        }

        override suspend fun getMessageHistory(conversationId: String): List<MessageEntity> {
            readHistorySnapshot = true
            operations += "history"
            return messages.toList()
        }

        override suspend fun getMessagesPage(conversationId: String, limit: Int, offset: Int) =
            messages.drop(offset).take(limit)

        override suspend fun getMessageCount(conversationId: String): Int = messages.size
        override suspend fun getOrCreateConversation(characterId: String, greeting: String) =
            ConversationResult(
                ConversationEntity(CONVERSATION_ID, characterId, "", 1L, 1L),
                greetingMessageId = null,
            )

        override suspend fun addMessage(conversationId: String, role: String, text: String): MessageEntity {
            operations += "add:$role:$text"
            return message("message-${nextMessageId++}", role, text, messages.size).also(messages::add)
        }

        override suspend fun deleteMessage(id: String) = Unit
        override suspend fun updateMessage(id: String, text: String) {
            operations += "update:$id:$text"
            val index = messages.indexOfFirst { it.id == id }
            check(index >= 0) { "Message not found: $id" }
            messages[index] = messages[index].copy(text = text)
        }

        override suspend fun setConversationError(conversationId: String, errorType: String, errorText: String) {
            operations += "set-error:$errorType:$errorText"
            this.errorType = errorType
            this.errorText = errorText
        }

        override suspend fun clearConversationError(conversationId: String) {
            operations += "clear-error"
            errorType = null
            errorText = ""
        }
    }

    private companion object {
        const val CONVERSATION_ID = "conversation-id"

        fun message(id: String, role: String, text: String, order: Int) = MessageEntity(
            id = id,
            conversationId = CONVERSATION_ID,
            role = role,
            text = text,
            timestamp = order.toLong(),
            orderInConversation = order,
        )
    }
}
