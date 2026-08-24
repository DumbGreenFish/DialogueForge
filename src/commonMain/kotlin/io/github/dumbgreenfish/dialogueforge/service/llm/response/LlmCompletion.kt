package io.github.dumbgreenfish.dialogueforge.service.llm.response

data class LlmCompletion(
    val text: String,
    val finishReason: String?,
)
