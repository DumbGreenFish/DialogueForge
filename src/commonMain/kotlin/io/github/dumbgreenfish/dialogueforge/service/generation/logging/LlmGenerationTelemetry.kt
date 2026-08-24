package io.github.dumbgreenfish.dialogueforge.service.generation.logging

import io.github.dumbgreenfish.dialogueforge.service.llm.response.LlmCompletion
import kotlinx.coroutines.CancellationException
import org.koin.core.annotation.Single
import kotlin.time.TimeSource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Single
@OptIn(ExperimentalUuidApi::class)
class LlmGenerationTelemetry(
    private val logger: GenerationLogger,
) {
    suspend fun execute(
        mode: GenerationMode,
        onUpdate: suspend (String) -> Unit,
        block: suspend (suspend (String) -> Unit) -> LlmCompletion,
    ): Result<String> {
        val requestId = Uuid.random().toString()
        val startedAt = TimeSource.Monotonic.markNow()
        var chunkCount = 0
        var characterCount = 0

        return try {
            logger.generationStarted(requestId, mode)
            val completion = block { response ->
                chunkCount += 1
                characterCount = response.length
                if (chunkCount == 1) {
                    logger.firstChunk(
                        requestId = requestId,
                        mode = mode,
                        elapsedMilliseconds = startedAt.elapsedNow().inWholeMilliseconds,
                    )
                }
                onUpdate(response)
            }
            characterCount = completion.text.length
            logger.generationCompleted(
                requestId = requestId,
                mode = mode,
                chunkCount = chunkCount,
                characterCount = characterCount,
                finishReason = completion.finishReason,
                elapsedMilliseconds = startedAt.elapsedNow().inWholeMilliseconds,
            )
            Result.success(completion.text)
        } catch (error: CancellationException) {
            logger.generationCancelled(
                requestId = requestId,
                mode = mode,
                chunkCount = chunkCount,
                characterCount = characterCount,
                elapsedMilliseconds = startedAt.elapsedNow().inWholeMilliseconds,
            )
            throw error
        } catch (error: Exception) {
            logger.generationFailed(
                requestId = requestId,
                mode = mode,
                chunkCount = chunkCount,
                characterCount = characterCount,
                elapsedMilliseconds = startedAt.elapsedNow().inWholeMilliseconds,
                error = error,
            )
            Result.failure(error)
        }
    }
}
