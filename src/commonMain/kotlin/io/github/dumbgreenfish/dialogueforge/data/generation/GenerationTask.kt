package io.github.dumbgreenfish.dialogueforge.data.generation

interface GenerationTask {
    suspend fun run(request: GenerationRequest): GenerationResult
}
