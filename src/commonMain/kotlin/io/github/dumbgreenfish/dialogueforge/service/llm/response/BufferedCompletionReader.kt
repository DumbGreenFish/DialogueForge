package io.github.dumbgreenfish.dialogueforge.service.llm.response

import io.github.dumbgreenfish.dialogueforge.data.dto.completion.ChatCompletionResponse
import io.github.dumbgreenfish.dialogueforge.data.dto.completion.LlmFinishReasonException
import io.github.dumbgreenfish.dialogueforge.data.dto.completion.LlmResponseException
import io.github.dumbgreenfish.dialogueforge.service.llm.transport.truncatedForDiagnostic
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class BufferedCompletionReader {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun read(response: HttpResponse): LlmCompletion {
        val responseBody = response.bodyAsText()
        val completion = try {
            json.decodeFromString<ChatCompletionResponse>(responseBody)
        } catch (_: Exception) {
            throw invalidResponse(response, responseBody)
        }
        val choice = completion.choices.firstOrNull()

        validateFinishReason(choice?.finishReason)

        val text = choice
            ?.message
            ?.content
            ?.takeIf(String::isNotBlank)
            ?: throw invalidResponse(response, responseBody)

        return LlmCompletion(
            text = text,
            finishReason = choice.finishReason,
        )
    }

    private fun invalidResponse(
        response: HttpResponse,
        responseBody: String,
    ) = LlmResponseException(
        statusCode = response.status.value,
        statusDescription = response.status.description,
        responseBody = responseBody.truncatedForDiagnostic(),
    )

    private fun validateFinishReason(finishReason: String?) {
        if (finishReason == "length" || finishReason == "content_filter") {
            throw LlmFinishReasonException(checkNotNull(finishReason))
        }
    }
}
