package io.github.dumbgreenfish.dialogueforge.service.generation.execution

import io.github.dumbgreenfish.dialogueforge.data.model.ConversationEntity
import io.github.dumbgreenfish.dialogueforge.data.model.MessageEntity
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.ConversationResult
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.DialogueRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConversationGenerationSessionCancellationTest {
    @Test
    fun assistant_write_finishes_when_cancelled_after_repository_write_starts() {
        runBlocking {
            val repository = SuspendingDialogueRepository()
            val session = ConversationGenerationSession(CONVERSATION_ID, repository)
            val job = launch {
                session.writeAssistantResponse("Saved partial")
            }

            withTimeout(TEST_TIMEOUT_MILLIS) {
                repository.addStarted.await()
            }
            assertTrue(repository.messages.isEmpty())

            job.cancel()
            repository.releaseAdd.complete(Unit)
            withTimeout(TEST_TIMEOUT_MILLIS) {
                job.join()
            }

            assertEquals(listOf("Saved partial"), repository.messages.map(MessageEntity::text))
        }
    }

    @Test
    fun cancel_clears_marker_from_an_already_cancelled_coroutine() {
        runBlocking {
            val repository = SuspendingDialogueRepository()
            val session = ConversationGenerationSession(CONVERSATION_ID, repository)
            val waitingForCancellation = CompletableDeferred<Unit>()
            val job = launch {
                try {
                    waitingForCancellation.complete(Unit)
                    awaitCancellation()
                } catch (error: kotlinx.coroutines.CancellationException) {
                    session.cancel()
                    throw error
                }
            }

            withTimeout(TEST_TIMEOUT_MILLIS) {
                waitingForCancellation.await()
            }
            job.cancel()
            withTimeout(TEST_TIMEOUT_MILLIS) {
                job.join()
            }

            assertEquals(1, repository.clearCalls)
        }
    }

    private class SuspendingDialogueRepository : DialogueRepository {
        val messages = mutableListOf<MessageEntity>()
        val addStarted = CompletableDeferred<Unit>()
        val releaseAdd = CompletableDeferred<Unit>()
        var clearCalls = 0

        override fun getMessages(conversationId: String): Flow<List<MessageEntity>> =
            flowOf(messages.toList())

        override suspend fun getMessageHistory(conversationId: String): List<MessageEntity> =
            messages.toList()

        override suspend fun getMessagesPage(conversationId: String, limit: Int, offset: Int) =
            messages.drop(offset).take(limit)

        override suspend fun getMessageCount(conversationId: String): Int = messages.size

        override suspend fun getOrCreateConversation(characterId: String, greeting: String) =
            ConversationResult(
                conversation = ConversationEntity(CONVERSATION_ID, characterId, "", 1L, 1L),
                greetingMessageId = null,
            )

        override suspend fun addMessage(
            conversationId: String,
            role: String,
            text: String,
        ): MessageEntity {
            addStarted.complete(Unit)
            releaseAdd.await()
            return MessageEntity(
                id = "assistant-id",
                conversationId = conversationId,
                role = role,
                text = text,
                timestamp = 1L,
                orderInConversation = 0,
            ).also(messages::add)
        }

        override suspend fun deleteMessage(id: String) = Unit
        override suspend fun updateMessage(id: String, text: String) = Unit
        override suspend fun setConversationError(
            conversationId: String,
            errorType: String,
            errorText: String,
        ) = Unit

        override suspend fun clearConversationError(conversationId: String) {
            yield()
            clearCalls += 1
        }
    }

    private companion object {
        const val CONVERSATION_ID = "conversation-id"
        const val TEST_TIMEOUT_MILLIS = 5_000L
    }
}
