package io.github.dumbgreenfish.dialogueforge.data.generation

import org.koin.core.annotation.Single

@Single(binds = [GenerationLifetime::class])
class WasmGenerationLifetime : GenerationLifetime {
    override fun activeGenerationsChanged(activeGenerations: List<GenerationRequest>) = Unit
}
