package io.github.dumbgreenfish.dialogueforge.service.llm.response

import io.github.dumbgreenfish.dialogueforge.data.dto.completion.ChatCompletionChunk
import io.github.dumbgreenfish.dialogueforge.data.dto.completion.LlmFinishReasonException
import io.github.dumbgreenfish.dialogueforge.data.dto.completion.LlmResponseException
import io.github.dumbgreenfish.dialogueforge.service.llm.transport.truncatedForDiagnostic
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.koin.core.annotation.Single

@Single
class StreamingCompletionReader {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun read(
        response: HttpResponse,
        onUpdate: suspend (String) -> Unit,
    ): LlmCompletion {
        val accumulated = StringBuilder()
        val dataLines = mutableListOf<String>()
        var done = false
        var finishReason: String? = null

        suspend fun processEvent() {
            if (dataLines.isEmpty()) return

            val eventData = dataLines.joinToString("\n")
            dataLines.clear()
            if (eventData == DONE_MARKER) {
                done = true
                return
            }

            val chunk = decodeChunk(response, eventData)
            chunk.choices.firstOrNull()?.let { choice ->
                choice.finishReason?.let { finishReason = it }
                choice.delta.content?.takeIf(String::isNotEmpty)?.let { delta ->
                    accumulated.append(delta)
                    onUpdate(accumulated.toString())
                }
            }
        }

        val channel = response.bodyAsChannel()
        while (!channel.isClosedForRead) {
            val line = channel.readUTF8Line() ?: break
            when {
                line.isEmpty() -> processEvent()
                line.startsWith(DATA_PREFIX) -> {
                    dataLines += line.removePrefix(DATA_PREFIX).removePrefix(" ")
                }
            }
        }
        processEvent()

        if (!done) {
            throw invalidResponse(response, "Stream ended without [DONE] marker")
        }
        if (finishReason == "length" || finishReason == "content_filter") {
            throw LlmFinishReasonException(checkNotNull(finishReason))
        }
        val text = accumulated.toString().takeIf(String::isNotBlank)
            ?: throw invalidResponse(response, "Stream completed without text content")

        return LlmCompletion(
            text = text,
            finishReason = finishReason,
        )
    }

    private fun decodeChunk(
        response: HttpResponse,
        eventData: String,
    ): ChatCompletionChunk = try {
        val element = json.parseToJsonElement(eventData)
        if ("error" in element.jsonObject) {
            throw IllegalStateException("Provider returned an error event")
        }
        json.decodeFromJsonElement(ChatCompletionChunk.serializer(), element)
    } catch (_: Exception) {
        throw invalidResponse(response, eventData)
    }

    private fun invalidResponse(
        response: HttpResponse,
        responseBody: String,
    ) = LlmResponseException(
        statusCode = response.status.value,
        statusDescription = response.status.description,
        responseBody = responseBody.truncatedForDiagnostic(),
    )

    private companion object {
        const val DATA_PREFIX = "data:"
        const val DONE_MARKER = "[DONE]"
    }
}
