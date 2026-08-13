@file:Suppress("DEPRECATION")

package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import io.github.dumbgreenfish.dialogueforge.data.generation.GenerationController
import io.github.dumbgreenfish.dialogueforge.data.generation.GenerationRequest
import io.github.dumbgreenfish.dialogueforge.data.generation.BackgroundGenerationSettings
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterEntity
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.ConversationEntity
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.ConversationResult
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.DialogueRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.MessageEntity
import io.github.dumbgreenfish.dialogueforge.testing.FakeSettingsRepository
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatError
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatErrorType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DialogueViewModelTest {
    @Test
    fun send_delegates_generation_and_reflects_active_conversation() = runBlocking {
        val generation = FakeGenerationController()
        val repository = FakeDialogueRepository()
        val viewModel = viewModel(repository, generation)
        load(viewModel)

        viewModel.handle(DialogueIntent.UpdateInput(TextFieldValue("Hello")))
        viewModel.handle(DialogueIntent.Send)

        assertEquals(
            GenerationRequest(CONVERSATION_ID, CHARACTER_ID, "Hello", "Airi"),
            generation.requests.single(),
        )
        assertEquals("", viewModel.state.value.inputText.text)
        assertTrue(withTimeout(TEST_TIMEOUT_MILLIS) { viewModel.state.first { it.isGenerating } }.isGenerating)
    }

    @Test
    fun first_android_send_waits_for_background_setup_choice() = runBlocking {
        val generation = FakeGenerationController()
        val backgroundSettings = FakeBackgroundGenerationSettings(shouldShowOnboarding = true)
        val viewModel = viewModel(FakeDialogueRepository(), generation, backgroundSettings)
        load(viewModel)

        viewModel.handle(DialogueIntent.UpdateInput(TextFieldValue("Hello")))
        viewModel.handle(DialogueIntent.Send)

        assertTrue(generation.requests.isEmpty())
        assertEquals("Hello", viewModel.state.value.inputText.text)
        assertEquals(BackgroundSetupStep.Introduction, viewModel.state.value.backgroundSetupStep)
    }

    @Test
    fun declining_background_setup_continues_pending_send_once_and_does_not_ask_again() = runBlocking {
        val generation = FakeGenerationController()
        val backgroundSettings = FakeBackgroundGenerationSettings(shouldShowOnboarding = true)
        val viewModel = viewModel(FakeDialogueRepository(), generation, backgroundSettings)
        load(viewModel)
        viewModel.handle(DialogueIntent.UpdateInput(TextFieldValue("Hello")))
        viewModel.handle(DialogueIntent.Send)

        viewModel.handle(DialogueIntent.DeclineBackgroundSetup)

        assertEquals(1, generation.requests.size)
        assertEquals("", viewModel.state.value.inputText.text)
        assertEquals(null, viewModel.state.value.backgroundSetupStep)
        assertEquals(1, backgroundSettings.completedCount)

        generation.finish(CONVERSATION_ID)
        viewModel.handle(DialogueIntent.UpdateInput(TextFieldValue("Again")))
        viewModel.handle(DialogueIntent.Send)

        assertEquals(2, generation.requests.size)
        assertEquals(1, backgroundSettings.completedCount)
    }

    @Test
    fun accepting_background_setup_requests_notifications_before_background_guidance() = runBlocking {
        val generation = FakeGenerationController()
        val backgroundSettings = FakeBackgroundGenerationSettings(shouldShowOnboarding = true)
        val viewModel = viewModel(FakeDialogueRepository(), generation, backgroundSettings)
        load(viewModel)
        viewModel.handle(DialogueIntent.UpdateInput(TextFieldValue("Hello")))
        viewModel.handle(DialogueIntent.Send)

        viewModel.handle(DialogueIntent.AcceptBackgroundSetup)

        assertEquals(1, backgroundSettings.notificationRequestCount)
        assertTrue(generation.requests.isEmpty())
        assertEquals(BackgroundSetupStep.BackgroundWork, viewModel.state.value.backgroundSetupStep)
    }

    @Test
    fun opening_background_settings_completes_setup_and_continues_pending_send() = runBlocking {
        val generation = FakeGenerationController()
        val backgroundSettings = FakeBackgroundGenerationSettings(shouldShowOnboarding = true)
        val viewModel = viewModel(FakeDialogueRepository(), generation, backgroundSettings)
        load(viewModel)
        viewModel.handle(DialogueIntent.UpdateInput(TextFieldValue("Hello")))
        viewModel.handle(DialogueIntent.Send)
        viewModel.handle(DialogueIntent.AcceptBackgroundSetup)

        viewModel.handle(DialogueIntent.OpenBackgroundSettings)

        assertEquals(1, backgroundSettings.backgroundSettingsOpenCount)
        assertEquals(1, backgroundSettings.completedCount)
        assertEquals(1, generation.requests.size)
        assertEquals(null, viewModel.state.value.backgroundSetupStep)
    }

    @Test
    fun invalid_send_does_not_start_background_setup() = runBlocking {
        val backgroundSettings = FakeBackgroundGenerationSettings(shouldShowOnboarding = true)
        val viewModel = viewModel(FakeDialogueRepository(), FakeGenerationController(), backgroundSettings)
        load(viewModel)

        viewModel.handle(DialogueIntent.Send)

        assertEquals(null, viewModel.state.value.backgroundSetupStep)
        assertEquals(0, backgroundSettings.completedCount)
    }

    @Test
    fun generation_update_reloads_messages_and_persisted_error_from_repository() = runBlocking {
        val generation = FakeGenerationController()
        val repository = FakeDialogueRepository()
        val viewModel = viewModel(repository, generation)
        load(viewModel)
        generation.start(GenerationRequest(CONVERSATION_ID, CHARACTER_ID, "Hello"))

        repository.messages += message("assistant-id", "assistant", "Completed response", 0)
        repository.setConversationError(CONVERSATION_ID, ChatErrorType.Server.name, "Persisted details")
        generation.finish(CONVERSATION_ID)

        val state = withTimeout(TEST_TIMEOUT_MILLIS) {
            viewModel.state.first {
                it.messages.firstOrNull()?.text == "Completed response" && it.chatError != null
            }
        }
        assertFalse(state.isGenerating)
        assertEquals(ChatError(ChatErrorType.Server, "Persisted details"), state.chatError)
    }

    @Test
    fun stop_cancels_only_the_open_conversation() = runBlocking {
        val generation = FakeGenerationController()
        val repository = FakeDialogueRepository()
        val viewModel = viewModel(repository, generation)
        load(viewModel)
        generation.start(GenerationRequest(CONVERSATION_ID, CHARACTER_ID, "Hello"))
        generation.start(GenerationRequest("other-conversation", "other-character", "Hi"))

        viewModel.handle(DialogueIntent.StopGeneration)

        assertEquals(listOf(CONVERSATION_ID), generation.cancelledConversationIds)
        assertEquals(setOf("other-conversation"), generation.activeConversationIds.value)
    }

    private fun viewModel(
        repository: FakeDialogueRepository,
        generation: FakeGenerationController,
        backgroundSettings: BackgroundGenerationSettings = FakeBackgroundGenerationSettings(false),
    ) = DialogueViewModel(
        characterRepository = FakeCharacterRepository(),
        dialogueRepository = repository,
        generationController = generation,
        settingsRepository = FakeSettingsRepository(),
        backgroundGenerationSettings = backgroundSettings,
        clipboardManager = TestClipboard(),
    )

    private class FakeBackgroundGenerationSettings(
        private var shouldShowOnboarding: Boolean,
    ) : BackgroundGenerationSettings {
        override val isAvailable: Boolean = shouldShowOnboarding
        var completedCount = 0
        var notificationRequestCount = 0
        var backgroundSettingsOpenCount = 0

        override fun shouldShowOnboarding(): Boolean = shouldShowOnboarding

        override fun completeOnboarding() {
            completedCount += 1
            shouldShowOnboarding = false
        }

        override fun requestNotificationPermission() {
            notificationRequestCount += 1
        }

        override fun configureNotifications() = Unit

        override fun openBackgroundSettings() {
            backgroundSettingsOpenCount += 1
        }
    }

    private suspend fun load(viewModel: DialogueViewModel) {
        viewModel.handle(DialogueIntent.LoadCharacter(CHARACTER_ID))
        withTimeout(TEST_TIMEOUT_MILLIS) {
            viewModel.state.first { !it.isLoading && it.conversationId == CONVERSATION_ID }
        }
    }

    private class FakeGenerationController : GenerationController {
        private val active = MutableStateFlow<Set<String>>(emptySet())
        override val activeConversationIds: StateFlow<Set<String>> = active
        override val changedConversationIds = MutableSharedFlow<String>(extraBufferCapacity = 16)
        val requests = mutableListOf<GenerationRequest>()
        val cancelledConversationIds = mutableListOf<String>()

        override fun start(request: GenerationRequest): Boolean {
            if (request.conversationId in active.value) return false
            requests += request
            active.value += request.conversationId
            return true
        }

        override fun cancel(conversationId: String) {
            cancelledConversationIds += conversationId
            active.value -= conversationId
        }

        override fun cancelAll() {
            cancelledConversationIds += active.value
            active.value = emptySet()
        }

        override fun interruptAll() {
            active.value = emptySet()
        }

        fun finish(conversationId: String) {
            active.value -= conversationId
            changedConversationIds.tryEmit(conversationId)
        }
    }

    private class FakeDialogueRepository : DialogueRepository {
        val messages = mutableListOf<MessageEntity>()
        private var conversation = ConversationEntity(CONVERSATION_ID, CHARACTER_ID, "", 1L, 1L)

        override fun getMessages(conversationId: String): Flow<List<MessageEntity>> =
            MutableStateFlow(messages)

        override suspend fun getMessagesPage(conversationId: String, limit: Int, offset: Int) =
            messages.asReversed().drop(offset).take(limit)

        override suspend fun getMessageCount(conversationId: String): Int = messages.size
        override suspend fun getConversation(conversationId: String): ConversationEntity = conversation
        override suspend fun getOrCreateConversation(characterId: String, greeting: String) =
            ConversationResult(conversation, greetingMessageId = null)

        override suspend fun addMessage(conversationId: String, role: String, text: String) =
            message("message-${messages.size}", role, text, messages.size).also(messages::add)

        override suspend fun deleteMessage(id: String) = Unit
        override suspend fun updateMessage(id: String, text: String) = Unit
        override suspend fun setConversationError(conversationId: String, errorType: String, errorText: String) {
            conversation = conversation.copy(
                hasError = true,
                errorType = ChatErrorType.valueOf(errorType),
                errorText = errorText,
            )
        }

        override suspend fun clearConversationError(conversationId: String) {
            conversation = conversation.copy(hasError = false, errorType = null, errorText = "")
        }
    }

    private class FakeCharacterRepository : CharacterRepository {
        override val characters: Flow<List<CharacterEntity>> = MutableStateFlow(emptyList())
        override suspend fun getById(id: String): CharacterEntity = character()
        override suspend fun import(data: io.github.dumbgreenfish.dialogueforge.data.model.TavernCardData) = Unit
        override suspend fun delete(id: String) = Unit
        override suspend fun togglePin(id: String) = Unit
        override suspend fun getMainImageThumbnail(id: String): ByteArray? = null
        override suspend fun getFullMainImage(id: String): ByteArray? = null
        override suspend fun getSizedThumbnail(id: String, maxDimension: Int): ByteArray? = null
        override suspend fun existsByName(name: String): Boolean = false
    }

    private class TestClipboard : ClipboardManager {
        private var text: AnnotatedString? = null
        override fun getText(): AnnotatedString? = text
        override fun setText(annotatedString: AnnotatedString) {
            text = annotatedString
        }
    }

    private companion object {
        const val CONVERSATION_ID = "conversation-id"
        const val CHARACTER_ID = "character-id"
        const val TEST_TIMEOUT_MILLIS = 5_000L

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
}
