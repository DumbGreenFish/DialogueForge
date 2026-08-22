package io.github.dumbgreenfish.dialogueforge.testing

import io.github.dumbgreenfish.dialogueforge.data.generation.GenerationLogger
import io.github.dumbgreenfish.dialogueforge.data.generation.GenerationMode

internal class RecordingGenerationLogger : GenerationLogger {
    val entries = mutableListOf<Entry>()

    override fun generationStarted(requestId: String, mode: GenerationMode) {
        entries += Entry("started", requestId, mode = mode.logValue)
    }

    override fun firstChunk(requestId: String, mode: GenerationMode, elapsedMilliseconds: Long) {
        entries += Entry(
            "first_chunk",
            requestId,
            elapsedMilliseconds = elapsedMilliseconds,
            mode = mode.logValue,
        )
    }

    override fun generationCompleted(
        requestId: String,
        mode: GenerationMode,
        chunkCount: Int,
        characterCount: Int,
        finishReason: String?,
        elapsedMilliseconds: Long,
    ) {
        entries += Entry(
            name = "completed",
            requestId = requestId,
            chunkCount = chunkCount,
            characterCount = characterCount,
            finishReason = finishReason,
            elapsedMilliseconds = elapsedMilliseconds,
            mode = mode.logValue,
        )
    }

    override fun generationFailed(
        requestId: String,
        mode: GenerationMode,
        chunkCount: Int,
        characterCount: Int,
        elapsedMilliseconds: Long,
        error: Throwable,
    ) {
        entries += Entry(
            name = "failed",
            requestId = requestId,
            chunkCount = chunkCount,
            characterCount = characterCount,
            elapsedMilliseconds = elapsedMilliseconds,
            errorType = error::class.simpleName,
            mode = mode.logValue,
        )
    }

    override fun generationCancelled(
        requestId: String,
        mode: GenerationMode,
        chunkCount: Int,
        characterCount: Int,
        elapsedMilliseconds: Long,
    ) {
        entries += Entry(
            name = "cancelled",
            requestId = requestId,
            chunkCount = chunkCount,
            characterCount = characterCount,
            elapsedMilliseconds = elapsedMilliseconds,
            mode = mode.logValue,
        )
    }

    override fun partialResponsePersistenceFailed(characterCount: Int, error: Throwable) {
        entries += Entry(
            name = "partial_persistence_failed",
            characterCount = characterCount,
            errorType = error::class.simpleName,
        )
    }

    data class Entry(
        val name: String,
        val requestId: String = "",
        val chunkCount: Int = 0,
        val characterCount: Int = 0,
        val finishReason: String? = null,
        val elapsedMilliseconds: Long = 0,
        val errorType: String? = null,
        val mode: String = "",
    )
}
