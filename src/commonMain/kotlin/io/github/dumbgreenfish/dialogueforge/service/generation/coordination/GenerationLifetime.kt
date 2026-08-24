package io.github.dumbgreenfish.dialogueforge.service.generation.coordination

import io.github.dumbgreenfish.dialogueforge.service.generation.api.GenerationRequest

interface GenerationLifetime {
    fun activeGenerationsChanged(activeGenerations: List<GenerationRequest>)
}
