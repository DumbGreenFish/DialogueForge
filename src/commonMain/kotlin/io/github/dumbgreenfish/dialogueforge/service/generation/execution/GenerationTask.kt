package io.github.dumbgreenfish.dialogueforge.service.generation.execution

import io.github.dumbgreenfish.dialogueforge.service.generation.api.GenerationRequest
import io.github.dumbgreenfish.dialogueforge.service.generation.api.GenerationResult

interface GenerationTask {
    suspend fun run(request: GenerationRequest): GenerationResult
}
