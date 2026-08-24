package io.github.dumbgreenfish.dialogueforge.service.generation

import io.github.dumbgreenfish.dialogueforge.service.generation.api.GenerationRequest as CanonicalGenerationRequest
import io.github.dumbgreenfish.dialogueforge.service.generation.coordination.GenerationLifetime as GenerationLifetimeContract
import org.koin.core.annotation.Single

@Single(binds = [GenerationLifetimeContract::class])
class DesktopGenerationLifetime : GenerationLifetimeContract {
    override fun activeGenerationsChanged(activeGenerations: List<CanonicalGenerationRequest>) = Unit
}
