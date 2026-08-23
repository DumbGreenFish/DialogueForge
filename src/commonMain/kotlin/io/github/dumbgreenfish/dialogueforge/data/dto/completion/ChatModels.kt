package io.github.dumbgreenfish.dialogueforge.data.dto.completion

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Float = 0.7f,
    @SerialName("max_tokens") val maxTokens: Int = 4096,
    val stream: Boolean = false,
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String,
)

@Serializable
data class ChatCompletionResponse(
    val choices: List<Choice> = emptyList(),
)

@Serializable
data class Choice(
    val message: ChatMessage? = null,
    @SerialName("finish_reason") val finishReason: String? = null,
)
