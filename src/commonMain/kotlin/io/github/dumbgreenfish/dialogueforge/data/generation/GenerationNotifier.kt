package io.github.dumbgreenfish.dialogueforge.data.generation

interface GenerationNotifier {
    fun completed(conversationId: String, result: GenerationResult.Success)
}
