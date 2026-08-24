package io.github.dumbgreenfish.dialogueforge.service.llm.request

import io.github.dumbgreenfish.dialogueforge.data.dto.completion.ChatCompletionRequest
import io.github.dumbgreenfish.dialogueforge.data.dto.completion.ChatMessage
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.SettingsRepository
import io.github.dumbgreenfish.dialogueforge.service.generation.logging.GenerationMode
import io.ktor.http.ContentType
import org.koin.core.annotation.Single

@Single
class ChatCompletionCallBuilder(
    private val settings: SettingsRepository,
) {
    suspend fun build(
        systemPrompt: String,
        history: List<Pair<String, String>>,
    ): ChatCompletionCall {
        val apiKey = settings.getApiKey()
            ?.takeIf(String::isNotBlank)
            ?: throw MissingApiKeyException()
        val streaming = settings.getStreamResponses()
        val messages = buildList {
            if (systemPrompt.isNotBlank()) {
                add(ChatMessage(role = "system", content = systemPrompt))
            }
            history.forEach { (role, content) ->
                add(ChatMessage(role = role, content = content))
            }
        }

        return ChatCompletionCall(
            endpoint = settings.getEndpoint(),
            apiKey = apiKey,
            request = ChatCompletionRequest(
                model = settings.getModel(),
                messages = messages,
                temperature = settings.getTemperature(),
                maxTokens = settings.getMaxTokens(),
                stream = streaming,
            ),
            responseContentType = if (streaming) {
                ContentType.Text.EventStream
            } else {
                ContentType.Application.Json
            },
            streaming = streaming,
            mode = if (streaming) GenerationMode.Streaming else GenerationMode.Buffered,
        )
    }
}
