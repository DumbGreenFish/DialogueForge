package io.github.dumbgreenfish.dialogueforge.service.generation.logging

interface GenerationLogger {
    fun generationStarted(requestId: String, mode: GenerationMode)
    fun firstChunk(requestId: String, mode: GenerationMode, elapsedMilliseconds: Long)

    fun generationCompleted(
        requestId: String,
        mode: GenerationMode,
        chunkCount: Int,
        characterCount: Int,
        finishReason: String?,
        elapsedMilliseconds: Long,
    )

    fun generationFailed(
        requestId: String,
        mode: GenerationMode,
        chunkCount: Int,
        characterCount: Int,
        elapsedMilliseconds: Long,
        error: Throwable,
    )

    fun generationCancelled(
        requestId: String,
        mode: GenerationMode,
        chunkCount: Int,
        characterCount: Int,
        elapsedMilliseconds: Long,
    )

    fun partialResponsePersistenceFailed(characterCount: Int, error: Throwable)
}
