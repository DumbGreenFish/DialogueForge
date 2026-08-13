package io.github.dumbgreenfish.dialogueforge.data.generation

import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterEntity
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.DialogueRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.SettingsRepository
import io.github.dumbgreenfish.dialogueforge.data.service.LlmResponseException
import io.github.dumbgreenfish.dialogueforge.data.service.LlmService
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatErrorType
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import kotlin.coroutines.cancellation.CancellationException

@Single(binds = [GenerationTask::class])
class MessageGenerationTask(
    private val characterRepository: CharacterRepository,
    private val dialogueRepository: DialogueRepository,
    private val llmService: LlmService,
    private val settingsRepository: SettingsRepository,
) : GenerationTask {
    override suspend fun run(request: GenerationRequest): GenerationResult {
        try {
            dialogueRepository.clearConversationError(request.conversationId)
            request.userText?.let { text ->
                dialogueRepository.addMessage(request.conversationId, USER_ROLE, text)
            }
            dialogueRepository.setConversationError(
                request.conversationId,
                ChatErrorType.Interrupted.name,
                "",
            )

            if (settingsRepository.getApiKey().isNullOrBlank()) {
                dialogueRepository.setConversationError(
                    request.conversationId,
                    ChatErrorType.NoApiKey.name,
                    "",
                )
                return GenerationResult.Failure
            }

            val character = checkNotNull(characterRepository.getById(request.characterId)) {
                "Character not found: ${request.characterId}"
            }
            val history = dialogueRepository.getMessages(request.conversationId)
                .first()
                .sortedBy { it.orderInConversation }
                .map { it.role to it.text }

            return llmService.chat(
                systemPrompt = systemPrompt(character),
                history = history,
            ).fold(
                onSuccess = { response ->
                    dialogueRepository.addMessage(request.conversationId, ASSISTANT_ROLE, response)
                    dialogueRepository.clearConversationError(request.conversationId)
                    GenerationResult.Success(
                        characterId = character.id,
                        characterName = character.name,
                        avatar = characterRepository.getMainImageThumbnail(character.id),
                        response = response,
                    )
                },
                onFailure = { throwable ->
                    val (type, details) = chatError(throwable)
                    dialogueRepository.setConversationError(request.conversationId, type.name, details)
                    GenerationResult.Failure
                },
            )
        } catch (e: CancellationException) {
            if (e is UserGenerationCancellationException) {
                withContext(NonCancellable) {
                    dialogueRepository.clearConversationError(request.conversationId)
                }
            }
            throw e
        } catch (e: Exception) {
            val (type, details) = chatError(e)
            dialogueRepository.setConversationError(request.conversationId, type.name, details)
            return GenerationResult.Failure
        }
    }

    private fun systemPrompt(character: CharacterEntity): String = buildString {
        appendLine("You are ${character.name}.")
        character.description.takeIf(String::isNotBlank)?.let {
            appendLine()
            appendLine(it)
        }
        character.personality.takeIf(String::isNotBlank)?.let {
            appendLine()
            appendLine("Personality: $it")
        }
        character.scenario.takeIf(String::isNotBlank)?.let {
            appendLine()
            appendLine("Scenario: $it")
        }
        appendLine()
        appendLine("Respond in character as ${character.name}. Stay consistent with the description and personality above.")
    }

    private fun chatError(error: Throwable): Pair<ChatErrorType, String> {
        val type = when (error) {
            is HttpRequestTimeoutException -> ChatErrorType.Network
            is LlmResponseException,
            is ClientRequestException,
            is ServerResponseException -> ChatErrorType.Server
            else -> ChatErrorType.Unknown
        }
        return type to error.message.orEmpty()
    }

    private companion object {
        const val USER_ROLE = "user"
        const val ASSISTANT_ROLE = "assistant"
    }
}
