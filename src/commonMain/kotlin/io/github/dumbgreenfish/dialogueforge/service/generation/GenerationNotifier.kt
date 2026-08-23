package io.github.dumbgreenfish.dialogueforge.service.generation

interface GenerationNotifier {
    fun completed(conversationId: String, result: GenerationResult.Success)
}
