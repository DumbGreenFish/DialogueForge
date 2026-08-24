package io.github.dumbgreenfish.dialogueforge.service.generation.execution

import io.github.dumbgreenfish.dialogueforge.data.model.CharacterEntity
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterRepository
import io.github.dumbgreenfish.dialogueforge.service.generation.api.GenerationRequest
import io.github.dumbgreenfish.dialogueforge.service.generation.api.GenerationResult
import io.github.dumbgreenfish.dialogueforge.service.generation.coordination.UserGenerationCancellationException
import io.github.dumbgreenfish.dialogueforge.service.llm.LlmClient
import kotlinx.coroutines.CancellationException
import org.koin.core.annotation.Single

@Single(binds = [GenerationTask::class])
class MessageGenerationTask(
    private val characterRepository: CharacterRepository,
    private val sessionFactory: ConversationGenerationSessionFactory,
    private val systemPromptBuilder: CharacterSystemPromptBuilder,
    private val llmClient: LlmClient,
    private val errorMapper: ChatErrorMapper,
) : GenerationTask {
    override suspend fun run(request: GenerationRequest): GenerationResult {
        val session = sessionFactory.create(request.conversationId)
        return try {
            val history = session.begin(request.userText)
            val character = loadCharacter(request.characterId)
            val result = llmClient.chat(
                systemPrompt = systemPromptBuilder.build(character),
                history = history,
                onUpdate = session::writeAssistantResponse,
            )
            val error = result.exceptionOrNull()
            if (error != null) {
                session.fail(errorMapper.map(error))
                return GenerationResult.Failure
            }

            val response = result.getOrThrow()
            session.complete(response)
            createSuccessResult(character, response)
        } catch (error: UserGenerationCancellationException) {
            session.cancel()
            throw error
        } catch (error: CancellationException) {
            session.interrupt()
            throw error
        } catch (error: Exception) {
            session.fail(errorMapper.map(error))
            GenerationResult.Failure
        }
    }

    private suspend fun loadCharacter(characterId: String): CharacterEntity =
        checkNotNull(characterRepository.getById(characterId)) {
            "Character not found: $characterId"
        }

    private suspend fun createSuccessResult(
        character: CharacterEntity,
        response: String,
    ): GenerationResult.Success = GenerationResult.Success(
        characterId = character.id,
        characterName = character.name,
        avatar = characterRepository.getMainImageThumbnail(character.id),
        response = response,
    )
}
