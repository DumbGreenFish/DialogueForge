package io.github.dumbgreenfish.dialogueforge.data.generation

import org.koin.core.annotation.Single

@Single(binds = [GenerationLifetime::class])
class DesktopGenerationLifetime : GenerationLifetime {
    override fun activeGenerationsChanged(activeGenerations: List<GenerationRequest>) = Unit
}
