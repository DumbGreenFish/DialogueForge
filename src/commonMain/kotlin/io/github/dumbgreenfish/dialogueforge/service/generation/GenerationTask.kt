package io.github.dumbgreenfish.dialogueforge.service.generation

interface GenerationTask {
    suspend fun run(
        request: GenerationRequest,
        onPartialResponse: (String) -> Unit = {},
    ): GenerationResult
}
