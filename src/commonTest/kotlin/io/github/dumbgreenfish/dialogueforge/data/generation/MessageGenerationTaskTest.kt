package io.github.dumbgreenfish.dialogueforge.data.generation

import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterEntity
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.ConversationEntity
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.ConversationResult
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.DialogueRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.MessageEntity
import io.github.dumbgreenfish.dialogueforge.data.service.LlmService
import io.github.dumbgreenfish.dialogueforge.testing.FakeSettingsRepository
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatErrorType
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MessageGenerationTaskTest {
    @Test
    fun successful_new_message_is_marked_interrupted_before_network_then_persisted_and_cleared() = runBlocking {
        val repository = FakeDialogueRepository()
        val settings = FakeSettingsRepository()
        val engine = MockEngine {
            assertEquals(ChatErrorType.Interrupted.name, repository.errorType)
            assertEquals("Hello", repository.messages.value.last().text)
            respond(
                content = successBody("Hi there"),
                status = HttpStatusCode.OK,
                headers = jsonHeaders,
            )
        }
        val task = task(repository, settings, engine)

        val result = task.run(request(userText = "Hello"))

        val success = assertIs<GenerationResult.Success>(result)
        assertEquals("Hi there", success.response)
        assertEquals(listOf("user", "assistant"), repository.messages.value.map { it.role })
        assertEquals(listOf("Hello", "Hi there"), repository.messages.value.map { it.text })
        assertNull(repository.errorType)
        assertTrue(repository.clearErrorCalls >= 2)
    }

    @Test
    fun generation_without_new_text_uses_existing_history_without_adding_a_user_message() = runBlocking {
        val repository = FakeDialogueRepository(
            initialMessages = listOf(message("existing", "user", "Existing prompt", 0)),
        )
        val engine = MockEngine {
            respond(successBody("Continuation"), HttpStatusCode.OK, jsonHeaders)
        }

        val result = task(repository, FakeSettingsRepository(), engine).run(request(userText = null))

        assertIs<GenerationResult.Success>(result)
        assertEquals(listOf("Existing prompt", "Continuation"), repository.messages.value.map { it.text })
    }

    @Test
    fun missing_api_key_finishes_with_persisted_no_api_key_error_without_calling_provider() = runBlocking {
        val repository = FakeDialogueRepository()
        var requestCount = 0
        val engine = MockEngine {
            requestCount += 1
            respond(successBody("Unexpected"), HttpStatusCode.OK, jsonHeaders)
        }

        val result = task(repository, FakeSettingsRepository(apiKey = ""), engine).run(request())

        assertEquals(GenerationResult.Failure, result)
        assertEquals(0, requestCount)
        assertEquals(ChatErrorType.NoApiKey.name, repository.errorType)
    }

    @Test
    fun provider_failure_is_persisted_and_does_not_add_an_assistant_message() = runBlocking {
        val repository = FakeDialogueRepository()
        val engine = MockEngine {
            respond(
                content = """{"error":{"message":"Provider unavailable"}}""",
                status = HttpStatusCode.ServiceUnavailable,
                headers = jsonHeaders,
            )
        }

        val result = task(repository, FakeSettingsRepository(), engine).run(request())

        assertEquals(GenerationResult.Failure, result)
        assertEquals(ChatErrorType.Server.name, repository.errorType)
        assertTrue(repository.errorText.contains("Provider unavailable"))
        assertEquals(listOf("user"), repository.messages.value.map { it.role })
    }

    @Test
    fun explicit_cancellation_clears_interrupted_marker_and_rethrows_cancellation() = runBlocking {
        val repository = FakeDialogueRepository()
        val requestStarted = CompletableDeferred<Unit>()
        val neverCompletes = CompletableDeferred<Unit>()
        val engine = MockEngine {
            requestStarted.complete(Unit)
            neverCompletes.await()
            respond(successBody("Unexpected"), HttpStatusCode.OK, jsonHeaders)
        }
        val task = task(repository, FakeSettingsRepository(), engine)

        val job = launch { task.run(request()) }
        withTimeout(TEST_TIMEOUT_MILLIS) { requestStarted.await() }
        job.cancel(UserGenerationCancellationException())
        job.join()

        assertNull(repository.errorType)
    }

    private fun task(
        repository: FakeDialogueRepository,
        settings: FakeSettingsRepository,
        engine: MockEngine,
    ) = MessageGenerationTask(
        characterRepository = FakeCharacterRepository(),
        dialogueRepository = repository,
        llmService = LlmService(settings, engine),
        settingsRepository = settings,
    )

    private fun request(userText: String? = "Hello") = GenerationRequest(
        conversationId = CONVERSATION_ID,
        characterId = CHARACTER_ID,
        userText = userText,
    )

    private class FakeDialogueRepository(
        initialMessages: List<MessageEntity> = emptyList(),
    ) : DialogueRepository {
        val messages = MutableStateFlow(initialMessages)
        var errorType: String? = null
        var errorText: String = ""
        var clearErrorCalls: Int = 0
        private var nextMessageId = initialMessages.size

        override fun getMessages(conversationId: String): Flow<List<MessageEntity>> = messages
        override suspend fun getMessagesPage(conversationId: String, limit: Int, offset: Int) =
            messages.value.asReversed().drop(offset).take(limit)

        override suspend fun getMessageCount(conversationId: String): Int = messages.value.size
        override suspend fun getOrCreateConversation(characterId: String, greeting: String) = ConversationResult(
            ConversationEntity(CONVERSATION_ID, characterId, "", 1L, 1L),
            greetingMessageId = null,
        )

        override suspend fun addMessage(conversationId: String, role: String, text: String): MessageEntity {
            val entity = message("message-${nextMessageId++}", role, text, messages.value.size)
            messages.value = messages.value + entity
            return entity
        }

        override suspend fun deleteMessage(id: String) = Unit
        override suspend fun updateMessage(id: String, text: String) = Unit

        override suspend fun setConversationError(conversationId: String, errorType: String, errorText: String) {
            this.errorType = errorType
            this.errorText = errorText
        }

        override suspend fun clearConversationError(conversationId: String) {
            clearErrorCalls += 1
            errorType = null
            errorText = ""
        }
    }

    private class FakeCharacterRepository : CharacterRepository {
        override val characters: Flow<List<CharacterEntity>> = MutableStateFlow(emptyList())
        override suspend fun getById(id: String): CharacterEntity = character()
        override suspend fun import(data: io.github.dumbgreenfish.dialogueforge.data.model.TavernCardData) = Unit
        override suspend fun delete(id: String) = Unit
        override suspend fun togglePin(id: String) = Unit
        override suspend fun getMainImageThumbnail(id: String): ByteArray = byteArrayOf(1, 2, 3)
        override suspend fun getFullMainImage(id: String): ByteArray? = null
        override suspend fun getSizedThumbnail(id: String, maxDimension: Int): ByteArray? = null
        override suspend fun existsByName(name: String): Boolean = false
    }

    private companion object {
        const val CONVERSATION_ID = "conversation-id"
        const val CHARACTER_ID = "character-id"
        const val TEST_TIMEOUT_MILLIS = 5_000L
        val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

        fun successBody(content: String) =
            """{"choices":[{"message":{"role":"assistant","content":"$content"}}]}"""

        fun message(id: String, role: String, text: String, order: Int) = MessageEntity(
            id = id,
            conversationId = CONVERSATION_ID,
            role = role,
            text = text,
            timestamp = order.toLong(),
            orderInConversation = order,
        )

        fun character() = CharacterEntity(
            id = CHARACTER_ID,
            name = "Airi",
            description = "Description",
            creator = "",
            avatarData = byteArrayOf(1, 2, 3),
            mainImageThumbnailData = byteArrayOf(1, 2, 3),
            thumbnailSmall = byteArrayOf(),
            thumbnailMedium = byteArrayOf(),
            thumbnailLarge = byteArrayOf(),
            tags = emptyList(),
            specVersion = "3.0",
            pinned = false,
            chatCount = 0,
            importedAt = 1L,
            updatedAt = 1L,
            lastUsedAt = null,
        )
    }
}
