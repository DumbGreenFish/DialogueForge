package io.github.dumbgreenfish.dialogueforge.service.llm.request

import io.github.dumbgreenfish.dialogueforge.data.dto.completion.ChatCompletionRequest
import io.github.dumbgreenfish.dialogueforge.service.generation.logging.GenerationMode
import io.ktor.http.ContentType

data class ChatCompletionCall(
    val endpoint: String,
    val apiKey: String,
    val request: ChatCompletionRequest,
    val responseContentType: ContentType,
    val streaming: Boolean,
    val mode: GenerationMode,
)
