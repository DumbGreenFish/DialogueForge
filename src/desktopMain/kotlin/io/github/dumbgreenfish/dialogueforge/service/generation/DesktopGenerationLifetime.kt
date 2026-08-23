package io.github.dumbgreenfish.dialogueforge.service.generation

import org.koin.core.annotation.Single

@Single(binds = [GenerationLifetime::class])
class DesktopGenerationLifetime : GenerationLifetime {
    override fun activeGenerationsChanged(activeGenerations: List<GenerationRequest>) = Unit
}
