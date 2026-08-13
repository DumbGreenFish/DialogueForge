package io.github.dumbgreenfish.dialogueforge.data.generation

interface GenerationLifetime {
    fun activeGenerationsChanged(activeGenerations: List<GenerationRequest>)
}
