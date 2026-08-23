package io.github.dumbgreenfish.dialogueforge.data.dto.completion

class LlmFinishReasonException(
    val finishReason: String,
) : Exception("LLM stream finished with reason: $finishReason")