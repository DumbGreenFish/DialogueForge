package io.github.dumbgreenfish.dialogueforge.service.generation

import io.github.dumbgreenfish.dialogueforge.service.generation.api.GenerationResult as CanonicalGenerationResult
import io.github.dumbgreenfish.dialogueforge.service.generation.coordination.GenerationNotifier as GenerationNotifierContract
import org.koin.core.annotation.Single

@Single(binds = [GenerationNotifierContract::class])
class WasmGenerationNotifier : GenerationNotifierContract, GenerationNotifier {
    override fun completed(conversationId: String, result: CanonicalGenerationResult.Success) = Unit
    override fun completed(conversationId: String, result: GenerationResult.Success) = Unit
}
