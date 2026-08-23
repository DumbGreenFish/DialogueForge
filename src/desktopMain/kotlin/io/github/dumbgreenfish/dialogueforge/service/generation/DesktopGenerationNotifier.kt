package io.github.dumbgreenfish.dialogueforge.service.generation

import org.koin.core.annotation.Single

@Single(binds = [GenerationNotifier::class])
class DesktopGenerationNotifier : GenerationNotifier {
    override fun completed(conversationId: String, result: GenerationResult.Success) = Unit
}
