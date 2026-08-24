package io.github.dumbgreenfish.dialogueforge.service.generation.execution

import io.github.dumbgreenfish.dialogueforge.data.dto.completion.LlmFinishReasonException
import io.github.dumbgreenfish.dialogueforge.data.dto.completion.LlmResponseException
import io.github.dumbgreenfish.dialogueforge.service.llm.request.MissingApiKeyException
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatError
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatErrorType
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import org.koin.core.annotation.Single

@Single
class ChatErrorMapper {
    fun map(error: Throwable): ChatError {
        val type = when (error) {
            is MissingApiKeyException -> ChatErrorType.NoApiKey
            is LlmFinishReasonException -> when (error.finishReason) {
                "length" -> ChatErrorType.TokenLimit
                "content_filter" -> ChatErrorType.ContentFilter
                else -> ChatErrorType.Server
            }
            is HttpRequestTimeoutException -> ChatErrorType.Network
            is LlmResponseException,
            is ClientRequestException,
            is ServerResponseException,
            -> ChatErrorType.Server
            else -> ChatErrorType.Unknown
        }
        return ChatError(
            type = type,
            details = error.message.orEmpty(),
        )
    }
}
