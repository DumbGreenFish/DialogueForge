package io.github.dumbgreenfish.dialogueforge.service.generation.coordination

import io.github.dumbgreenfish.dialogueforge.service.generation.api.GenerationResult

interface GenerationNotifier {
    fun completed(conversationId: String, result: GenerationResult.Success)
}
