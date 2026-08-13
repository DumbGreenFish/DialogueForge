@file:Suppress("DEPRECATION")

package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DialogueViewModelTest {
    @Test
    fun llm_response_error_is_classified_as_server_and_persisted_without_losing_details() = runBlocking {
        val responseBody = """{"error":{"message":"Blocked by provider","code":"content_filter"}}"""
        val expectedDetails = "HTTP 400 Bad Request\n$responseBody"
        var persistedType: String? = null
        var persistedDetails: String? = null
        val conversation = ConversationEntity(
            id = "conversation-id",
            characterId = "character-id",
            title = "",
            createdAt = 1L,
            updatedAt = 1L,
        )
        val dialogueRepository = object : DialogueRepository {
            override fun getMessages(conversationId: String): Flow<List<MessageEntity>> = flowOf(emptyList())
            override suspend fun getMessagesPage(conversationId: String, limit: Int, offset: Int) = emptyList<MessageEntity>()
            override suspend fun getMessageCount(conversationId: String) = 0
            override suspend fun getOrCreateConversation(characterId: String, greeting: String) =
                ConversationResult(conversation, greetingMessageId = null)

            override suspend fun addMessage(conversationId: String, role: String, text: String) = MessageEntity(
                id = "message-id",
                conversationId = conversationId,
                role = role,
                text = text,
                timestamp = 2L,
                orderInConversation = 0,
            )

            override suspend fun deleteMessage(id: String) = Unit
            override suspend fun updateMessage(id: String, text: String) = Unit
            override suspend fun setConversationError(conversationId: String, errorType: String, errorText: String) {
                persistedType = errorType
                persistedDetails = errorText
            }

            override suspend fun clearConversationError(conversationId: String) = Unit
        }
        val characterRepository = object : CharacterRepository {
            override val characters: Flow<List<CharacterEntity>> = flowOf(emptyList())
            override suspend fun getById(id: String) = testCharacter()
            override suspend fun import(data: io.github.dumbgreenfish.dialogueforge.data.model.TavernCardData) = Unit
            override suspend fun delete(id: String) = Unit
            override suspend fun togglePin(id: String) = Unit
            override suspend fun getMainImageThumbnail(id: String): ByteArray? = null
            override suspend fun getFullMainImage(id: String): ByteArray? = null
            override suspend fun getSizedThumbnail(id: String, maxDimension: Int): ByteArray? = null
            override suspend fun existsByName(name: String) = false
        }
        val settings = FakeSettingsRepository()
        val engine = MockEngine {
            respond(
                content = responseBody,
                status = HttpStatusCode.BadRequest,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val clipboard = object : ClipboardManager {
            private var text: AnnotatedString? = null
            override fun getText(): AnnotatedString? = text
            override fun setText(annotatedString: AnnotatedString) {
                text = annotatedString
            }
        }
        val viewModel = DialogueViewModel(
            characterRepository = characterRepository,
            dialogueRepository = dialogueRepository,
            llmService = LlmService(settings, engine),
            settingsRepository = settings,
            clipboardManager = clipboard,
        )

        viewModel.handle(DialogueIntent.LoadCharacter("character-id"))
        withTimeout(5_000) { viewModel.state.first { !it.isLoading && it.conversationId != null } }
        viewModel.handle(DialogueIntent.UpdateInput(TextFieldValue("Hello")))
        viewModel.handle(DialogueIntent.Send)
        val error = assertNotNull(withTimeout(5_000) { viewModel.state.first { it.chatError != null }.chatError })

        assertEquals(ChatErrorType.Server, error.type)
        assertEquals(expectedDetails, error.details)
        assertEquals(ChatErrorType.Server.name, persistedType)
        assertEquals(expectedDetails, persistedDetails)
    }

    private fun testCharacter() = CharacterEntity(
        id = "character-id",
        name = "Test character",
        description = "",
        creator = "",
        avatarData = byteArrayOf(),
        mainImageThumbnailData = byteArrayOf(),
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
