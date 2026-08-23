package io.github.dumbgreenfish.dialogueforge.service.generation

import co.touchlab.kermit.Logger
import org.koin.core.annotation.Single

@Single(binds = [GenerationLogger::class])
class KermitGenerationLogger : GenerationLogger {
    private val logger = Logger.withTag("LlmGeneration")

    override fun generationStarted(requestId: String, mode: GenerationMode) {
        logger.i { "event=generation_started mode=${mode.logValue} request_id=$requestId" }
    }

    override fun firstChunk(requestId: String, mode: GenerationMode, elapsedMilliseconds: Long) {
        logger.i { "event=first_chunk mode=${mode.logValue} request_id=$requestId elapsed_ms=$elapsedMilliseconds" }
    }

    override fun generationCompleted(
        requestId: String,
        mode: GenerationMode,
        chunkCount: Int,
        characterCount: Int,
        finishReason: String?,
        elapsedMilliseconds: Long,
    ) {
        logger.i {
            "event=generation_completed mode=${mode.logValue} request_id=$requestId " +
                "chunks=$chunkCount characters=$characterCount " +
                "finish_reason=${finishReason.orEmpty()} elapsed_ms=$elapsedMilliseconds"
        }
    }

    override fun generationFailed(
        requestId: String,
        mode: GenerationMode,
        chunkCount: Int,
        characterCount: Int,
        elapsedMilliseconds: Long,
        error: Throwable,
    ) {
        logger.e {
            "event=generation_failed mode=${mode.logValue} request_id=$requestId " +
                "chunks=$chunkCount characters=$characterCount " +
                "elapsed_ms=$elapsedMilliseconds error_type=${error::class.simpleName}"
        }
    }

    override fun generationCancelled(
        requestId: String,
        mode: GenerationMode,
        chunkCount: Int,
        characterCount: Int,
        elapsedMilliseconds: Long,
    ) {
        logger.i {
            "event=generation_cancelled mode=${mode.logValue} request_id=$requestId " +
                "chunks=$chunkCount characters=$characterCount " +
                "elapsed_ms=$elapsedMilliseconds"
        }
    }

    override fun partialResponsePersistenceFailed(characterCount: Int, error: Throwable) {
        logger.e {
            "event=partial_persistence_failed characters=$characterCount error_type=${error::class.simpleName}"
        }
    }
}
