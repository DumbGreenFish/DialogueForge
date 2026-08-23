package io.github.dumbgreenfish.dialogueforge.service.generation

interface GenerationLifetime {
    fun activeGenerationsChanged(activeGenerations: List<GenerationRequest>)
}
