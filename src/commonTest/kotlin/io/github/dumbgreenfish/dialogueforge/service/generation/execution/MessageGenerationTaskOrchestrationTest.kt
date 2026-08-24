package io.github.dumbgreenfish.dialogueforge.service.generation.execution

import io.github.dumbgreenfish.dialogueforge.data.dto.card.TavernCardData
import io.github.dumbgreenfish.dialogueforge.data.dto.completion.LlmResponseException
import io.github.dumbgreenfish.dialogueforge.data.model.CharacterEntity
import io.github.dumbgreenfish.dialogueforge.data.model.ConversationEntity
import io.github.dumbgreenfish.dialogueforge.data.model.MessageEntity
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.ConversationResult
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.DialogueRepository
import io.github.dumbgreenfish.dialogueforge.service.generation.api.GenerationRequest
import io.github.dumbgreenfish.dialogueforge.service.generation.api.GenerationResult
import io.github.dumbgreenfish.dialogueforge.service.generation.coordination.UserGenerationCancellationException
import io.github.dumbgreenfish.dialogueforge.service.llm.LlmClient
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatErrorType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MessageGenerationTaskOrchestrationTest {
    @Test
    fun buffered_success_passes_snapshot_and_prompt_to_llm_persists_response_and_returns_character_data() {
        runBlocking {
            val repository = RecordingDialogueRepository()
            var capturedPrompt = ""
            var capturedHistory = emptyList<Pair<String, String>>()
            val client = FakeLlmClient { systemPrompt, history, _ ->
                capturedPrompt = systemPrompt
                capturedHistory = history
                assertEquals(ChatErrorType.Interrupted.name, repository.errorType)
                Result.success("Buffered answer")
            }

            val result = task(repository, client).run(request())

            val success = assertIs<GenerationResult.Success>(result)
            assertEquals("Buffered answer", success.response)
            assertEquals(CHARACTER_ID, success.characterId)
            assertEquals("Airi", success.characterName)
            assertTrue(success.avatar?.contentEquals(AVATAR) == true)
            assertEquals(listOf("user" to "Hello"), capturedHistory)
            assertTrue(capturedPrompt.startsWith("You are Airi."))
            assertEquals(listOf("user", "assistant"), repository.messages.map(MessageEntity::role))
            assertEquals(listOf("Hello", "Buffered answer"), repository.messages.map(MessageEntity::text))
            assertNull(repository.errorType)
        }
    }

    @Test
    fun llm_failure_after_update_keeps_one_partial_message_and_persists_mapped_error() {
        runBlocking {
            val repository = RecordingDialogueRepository()
            val client = FakeLlmClient { _, _, onUpdate ->
                onUpdate("Part")
                onUpdate("Partial answer")
                Result.failure(LlmResponseException(503, "Unavailable", "Provider unavailable"))
            }

            val result = task(repository, client).run(request())

            assertEquals(GenerationResult.Failure, result)
            assertEquals(listOf("user", "assistant"), repository.messages.map(MessageEntity::role))
            assertEquals("Partial answer", repository.messages.last().text)
            assertEquals(ChatErrorType.Server.name, repository.errorType)
            assertTrue(repository.errorText.contains("Provider unavailable"))
        }
    }

    @Test
    fun user_cancellation_clears_interrupted_marker_and_rethrows() {
        runBlocking {
            val repository = RecordingDialogueRepository()
            val client = FakeLlmClient { _, _, onUpdate ->
                onUpdate("Saved partial")
                throw UserGenerationCancellationException()
            }

            val error = try {
                task(repository, client).run(request())
                null
            } catch (error: Throwable) {
                error
            }

            assertIs<UserGenerationCancellationException>(error)
            assertNull(repository.errorType)
            assertEquals("Saved partial", repository.messages.last().text)
        }
    }

    @Test
    fun ordinary_cancellation_keeps_interrupted_marker_and_rethrows() {
        runBlocking {
            val repository = RecordingDialogueRepository()
            val cancellation = CancellationException("service stopped")
            val client = FakeLlmClient { _, _, onUpdate ->
                onUpdate("Interrupted partial")
                throw cancellation
            }

            val error = try {
                task(repository, client).run(request())
                null
            } catch (error: Throwable) {
                error
            }

            assertEquals(cancellation, error)
            assertEquals(ChatErrorType.Interrupted.name, repository.errorType)
            assertEquals("Interrupted partial", repository.messages.last().text)
        }
    }

    @Test
    fun exception_outside_llm_result_is_mapped_and_persisted() {
        runBlocking {
            val repository = RecordingDialogueRepository()
            val result = task(
                repository = repository,
                client = FakeLlmClient { _, _, _ -> Result.success("Unexpected") },
                character = null,
            ).run(request())

            assertEquals(GenerationResult.Failure, result)
            assertEquals(ChatErrorType.Unknown.name, repository.errorType)
            assertTrue(repository.errorText.contains("Character not found"))
            assertEquals(listOf("user"), repository.messages.map(MessageEntity::role))
        }
    }

    private fun task(
        repository: RecordingDialogueRepository,
        client: LlmClient,
        character: CharacterEntity? = character(),
    ) = MessageGenerationTask(
        characterRepository = FakeCharacterRepository(character),
        sessionFactory = ConversationGenerationSessionFactory(repository),
        systemPromptBuilder = CharacterSystemPromptBuilder(),
        llmClient = client,
        errorMapper = ChatErrorMapper(),
    )

    private fun request() = GenerationRequest(
        conversationId = CONVERSATION_ID,
        characterId = CHARACTER_ID,
        userText = "Hello",
    )

    private class FakeLlmClient(
        private val behavior: suspend (
            String,
            List<Pair<String, String>>,
            suspend (String) -> Unit,
        ) -> Result<String>,
    ) : LlmClient {
        override suspend fun chat(
            systemPrompt: String,
            history: List<Pair<String, String>>,
            onUpdate: suspend (String) -> Unit,
        ): Result<String> = behavior(systemPrompt, history, onUpdate)
    }

    private class RecordingDialogueRepository : DialogueRepository {
        val messages = mutableListOf<MessageEntity>()
        var errorType: String? = null
        var errorText: String = ""
        private var nextMessageId = 0

        override fun getMessages(conversationId: String): Flow<List<MessageEntity>> =
            flowOf(messages.toList())

        override suspend fun getMessageHistory(conversationId: String): List<MessageEntity> =
            messages.toList()

        override suspend fun getMessagesPage(conversationId: String, limit: Int, offset: Int) =
            messages.asReversed().drop(offset).take(limit)

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
            return message("message-${nextMessageId++}", role, text, messages.size).also(messages::add)
        }

        override suspend fun deleteMessage(id: String) = Unit

        override suspend fun updateMessage(id: String, text: String) {
            val index = messages.indexOfFirst { it.id == id }
            check(index >= 0) { "Message not found: $id" }
            messages[index] = messages[index].copy(text = text)
        }

        override suspend fun setConversationError(
            conversationId: String,
            errorType: String,
            errorText: String,
        ) {
            this.errorType = errorType
            this.errorText = errorText
        }

        override suspend fun clearConversationError(conversationId: String) {
            errorType = null
            errorText = ""
        }
    }

    private class FakeCharacterRepository(
        private val character: CharacterEntity?,
    ) : CharacterRepository {
        override val characters: Flow<List<CharacterEntity>> = flowOf(listOfNotNull(character))
        override suspend fun getById(id: String): CharacterEntity? = character
        override suspend fun import(data: TavernCardData) = Unit
        override suspend fun delete(id: String) = Unit
        override suspend fun togglePin(id: String) = Unit
        override suspend fun getMainImageThumbnail(id: String): ByteArray? = AVATAR
        override suspend fun getFullMainImage(id: String): ByteArray? = null
        override suspend fun getSizedThumbnail(id: String, maxDimension: Int): ByteArray? = null
        override suspend fun existsByName(name: String): Boolean = false
    }

    private companion object {
        const val CONVERSATION_ID = "conversation-id"
        const val CHARACTER_ID = "character-id"
        val AVATAR = byteArrayOf(1, 2, 3)

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
            avatarData = AVATAR,
            mainImageThumbnailData = AVATAR,
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
            personality = "Curious",
            scenario = "Workshop",
        )
    }
}
