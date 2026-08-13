package io.github.dumbgreenfish.dialogueforge.data.generation

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MessageGenerationCoordinatorTest {
    @Test
    fun different_conversations_generate_concurrently_and_share_one_lifetime() = runBlocking {
        val task = ControlledGenerationTask()
        val lifetime = RecordingGenerationLifetime()
        val coordinator = coordinator(task, lifetime = lifetime)

        assertTrue(coordinator.start(request("conversation-a", "character-a")))
        assertTrue(coordinator.start(request("conversation-b", "character-b")))
        task.awaitStarted("conversation-a")
        task.awaitStarted("conversation-b")

        assertEquals(setOf("conversation-a", "conversation-b"), coordinator.activeConversationIds.value)
        assertEquals(listOf(1, 2), lifetime.activeCounts)

        task.complete("conversation-a")
        withTimeout(TEST_TIMEOUT_MILLIS) {
            coordinator.activeConversationIds.first { it == setOf("conversation-b") }
        }
        task.complete("conversation-b")
        withTimeout(TEST_TIMEOUT_MILLIS) {
            coordinator.activeConversationIds.first { it.isEmpty() }
        }

        assertEquals(listOf(1, 2, 1, 0), lifetime.activeCounts)
    }

    @Test
    fun a_second_generation_for_the_same_conversation_is_rejected() {
        runBlocking {
            val task = ControlledGenerationTask()
            val coordinator = coordinator(task)
            val first = request("conversation-a", "character-a")

            assertTrue(coordinator.start(first))
            task.awaitStarted(first.conversationId)
            assertFalse(coordinator.start(first.copy(userText = "Second")))
            assertEquals(1, task.startCount(first.conversationId))

            coordinator.cancel(first.conversationId)
            withTimeout(TEST_TIMEOUT_MILLIS) {
                coordinator.activeConversationIds.first { it.isEmpty() }
            }
        }
    }

    @Test
    fun cancelling_one_conversation_does_not_cancel_another() {
        runBlocking {
            val task = ControlledGenerationTask()
            val coordinator = coordinator(task)

            coordinator.start(request("conversation-a", "character-a"))
            coordinator.start(request("conversation-b", "character-b"))
            task.awaitStarted("conversation-a")
            task.awaitStarted("conversation-b")

            coordinator.cancel("conversation-a")
            task.awaitCancelled("conversation-a")

            assertEquals(setOf("conversation-b"), coordinator.activeConversationIds.value)
            assertFalse(task.wasCancelled("conversation-b"))

            task.complete("conversation-b")
            withTimeout(TEST_TIMEOUT_MILLIS) {
                coordinator.activeConversationIds.first { it.isEmpty() }
            }
        }
    }

    @Test
    fun cancel_all_cancels_every_active_generation() {
        runBlocking {
            val task = ControlledGenerationTask()
            val coordinator = coordinator(task)

            coordinator.start(request("conversation-a", "character-a"))
            coordinator.start(request("conversation-b", "character-b"))
            task.awaitStarted("conversation-a")
            task.awaitStarted("conversation-b")

            coordinator.cancelAll()

            task.awaitCancelled("conversation-a")
            task.awaitCancelled("conversation-b")
            withTimeout(TEST_TIMEOUT_MILLIS) {
                coordinator.activeConversationIds.first { it.isEmpty() }
            }
        }
    }

    @Test
    fun only_a_successful_generation_posts_a_completion() = runBlocking {
        val task = ControlledGenerationTask()
        val notifier = RecordingGenerationNotifier()
        val coordinator = coordinator(task, notifier = notifier)

        coordinator.start(request("successful", "character-a"))
        coordinator.start(request("failed", "character-b"))
        task.awaitStarted("successful")
        task.awaitStarted("failed")
        task.complete("successful", GenerationResult.Success("character-a", "Airi", byteArrayOf(1), "Hello"))
        task.complete("failed", GenerationResult.Failure)
        withTimeout(TEST_TIMEOUT_MILLIS) {
            coordinator.activeConversationIds.first { it.isEmpty() }
        }

        assertEquals(listOf("successful"), notifier.conversationIds)
    }

    private fun CoroutineScope.coordinator(
        task: GenerationTask,
        lifetime: GenerationLifetime = RecordingGenerationLifetime(),
        notifier: GenerationNotifier = RecordingGenerationNotifier(),
    ) = MessageGenerationCoordinator(task, lifetime, notifier, this)

    private fun request(conversationId: String, characterId: String) = GenerationRequest(
        conversationId = conversationId,
        characterId = characterId,
        userText = "Hello",
    )

    private class ControlledGenerationTask : GenerationTask {
        private val starts = mutableMapOf<String, Int>()
        private val started = mutableMapOf<String, CompletableDeferred<Unit>>()
        private val results = mutableMapOf<String, CompletableDeferred<GenerationResult>>()
        private val cancelled = mutableMapOf<String, CompletableDeferred<Unit>>()

        override suspend fun run(request: GenerationRequest): GenerationResult {
            val id = request.conversationId
            starts[id] = (starts[id] ?: 0) + 1
            started.getOrPut(id) { CompletableDeferred() }.complete(Unit)
            return try {
                results.getOrPut(id) { CompletableDeferred() }.await()
            } finally {
                if (!results.getOrPut(id) { CompletableDeferred() }.isCompleted) {
                    cancelled.getOrPut(id) { CompletableDeferred() }.complete(Unit)
                }
            }
        }

        suspend fun awaitStarted(id: String) = withTimeout(TEST_TIMEOUT_MILLIS) {
            started.getOrPut(id) { CompletableDeferred() }.await()
        }

        suspend fun awaitCancelled(id: String) = withTimeout(TEST_TIMEOUT_MILLIS) {
            cancelled.getOrPut(id) { CompletableDeferred() }.await()
        }

        fun complete(
            id: String,
            result: GenerationResult = GenerationResult.Success(id, id, null, "Response"),
        ) {
            results.getOrPut(id) { CompletableDeferred() }.complete(result)
        }

        fun startCount(id: String): Int = starts[id] ?: 0
        fun wasCancelled(id: String): Boolean = cancelled[id]?.isCompleted == true
    }

    private class RecordingGenerationLifetime : GenerationLifetime {
        val activeCounts = mutableListOf<Int>()
        override fun activeGenerationsChanged(activeGenerations: List<GenerationRequest>) {
            activeCounts += activeGenerations.size
        }
    }

    private class RecordingGenerationNotifier : GenerationNotifier {
        val conversationIds = mutableListOf<String>()
        override fun completed(conversationId: String, result: GenerationResult.Success) {
            conversationIds += conversationId
        }
    }

    private companion object {
        const val TEST_TIMEOUT_MILLIS = 5_000L
    }
}
