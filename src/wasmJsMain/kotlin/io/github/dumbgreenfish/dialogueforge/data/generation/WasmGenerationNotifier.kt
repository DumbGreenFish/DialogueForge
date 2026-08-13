package io.github.dumbgreenfish.dialogueforge.data.generation

import org.koin.core.annotation.Single

@Single(binds = [GenerationNotifier::class])
class WasmGenerationNotifier : GenerationNotifier {
    override fun completed(conversationId: String, result: GenerationResult.Success) = Unit
}
