package io.github.dumbgreenfish.dialogueforge.data.service

class LlmFinishReasonException(
    val finishReason: String,
) : Exception("LLM stream finished with reason: $finishReason")
