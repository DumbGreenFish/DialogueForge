package io.github.dumbgreenfish.dialogueforge.service.generation

import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.DialogueRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.SettingsRepository
import io.github.dumbgreenfish.dialogueforge.service.LlmClient
import io.github.dumbgreenfish.dialogueforge.service.generation.api.GenerationResult as CanonicalGenerationResult
import io.github.dumbgreenfish.dialogueforge.service.generation.execution.CharacterSystemPromptBuilder
import io.github.dumbgreenfish.dialogueforge.service.generation.execution.ChatErrorMapper
import io.github.dumbgreenfish.dialogueforge.service.generation.execution.ConversationGenerationSession
import io.github.dumbgreenfish.dialogueforge.service.generation.execution.ConversationGenerationSessionFactory
import io.github.dumbgreenfish.dialogueforge.service.generation.execution.GenerationSession
import io.github.dumbgreenfish.dialogueforge.service.generation.execution.MessageGenerationTask as CanonicalMessageGenerationTask
import io.github.dumbgreenfish.dialogueforge.service.llm.LlmClient as CanonicalLlmClient
import io.github.dumbgreenfish.dialogueforge.service.llm.request.MissingApiKeyException
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatError
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class MessageGenerationTask(
    private val characterRepository: CharacterRepository,
    private val dialogueRepository: DialogueRepository,
    private val llmClient: LlmClient,
    private val settingsRepository: SettingsRepository,
    private val logger: GenerationLogger,
) : GenerationTask {
    override suspend fun run(
        request: GenerationRequest,
        onPartialResponse: (String) -> Unit,
    ): GenerationResult {
        val state = LegacyGenerationState()
        val delegate = CanonicalMessageGenerationTask(
            characterRepository = characterRepository,
            sessionFactory = LegacyConversationGenerationSessionFactory(
                repository = dialogueRepository,
                logger = logger,
                state = state,
            ),
            systemPromptBuilder = CharacterSystemPromptBuilder(),
            llmClient = LegacyLlmClientAdapter(
                delegate = llmClient,
                settings = settingsRepository,
                state = state,
                onPartialResponse = onPartialResponse,
            ),
            errorMapper = ChatErrorMapper(),
        )
        return when (val result = delegate.run(request)) {
            CanonicalGenerationResult.Failure -> GenerationResult.Failure
            is CanonicalGenerationResult.Success -> GenerationResult.Success(
                characterId = result.characterId,
                characterName = result.characterName,
                avatar = result.avatar,
                response = result.response,
            )
        }
    }
}

private class LegacyLlmClientAdapter(
    private val delegate: LlmClient,
    private val settings: SettingsRepository,
    private val state: LegacyGenerationState,
    private val onPartialResponse: (String) -> Unit,
) : CanonicalLlmClient {
    override suspend fun chat(
        systemPrompt: String,
        history: List<Pair<String, String>>,
        onUpdate: suspend (String) -> Unit,
    ): Result<String> {
        if (settings.getApiKey().isNullOrBlank()) {
            return Result.failure(MissingApiKeyException())
        }
        return delegate.chat(
            systemPrompt = systemPrompt,
            history = history,
            onUpdate = { response ->
                state.partialResponse = response
                onPartialResponse(response)
            },
        )
    }
}

private class LegacyConversationGenerationSessionFactory(
    private val repository: DialogueRepository,
    private val logger: GenerationLogger,
    private val state: LegacyGenerationState,
) : ConversationGenerationSessionFactory(repository) {
    override fun create(conversationId: String): GenerationSession =
        LegacyConversationGenerationSession(
            conversationId = conversationId,
            repository = repository,
            logger = logger,
            state = state,
        )
}

private class LegacyConversationGenerationSession(
    private val conversationId: String,
    private val repository: DialogueRepository,
    private val logger: GenerationLogger,
    private val state: LegacyGenerationState,
) : GenerationSession {
    private val canonicalSession = ConversationGenerationSession(conversationId, repository)

    override suspend fun begin(userText: String?): List<Pair<String, String>> =
        canonicalSession.begin(userText)

    override suspend fun writeAssistantResponse(text: String) {
        state.partialResponse = text
    }

    override suspend fun complete(response: String) {
        repository.addMessage(conversationId, ASSISTANT_ROLE, response)
        repository.clearConversationError(conversationId)
    }

    override suspend fun fail(error: ChatError) {
        persistPartialResponse()
        repository.setConversationError(
            conversationId = conversationId,
            errorType = error.type.name,
            errorText = error.details,
        )
    }

    override suspend fun cancel() {
        withContext(NonCancellable) {
            persistPartialResponse()
            repository.clearConversationError(conversationId)
        }
    }

    override suspend fun interrupt() {
        withContext(NonCancellable) {
            persistPartialResponse()
        }
    }

    private suspend fun persistPartialResponse() {
        val response = state.partialResponse
        if (response.isBlank()) return
        try {
            repository.addMessage(conversationId, ASSISTANT_ROLE, response)
        } catch (error: Exception) {
            logger.partialResponsePersistenceFailed(response.length, error)
        }
    }

    private companion object {
        const val ASSISTANT_ROLE = "assistant"
    }
}

private class LegacyGenerationState {
    var partialResponse: String = ""
}
