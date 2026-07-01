package io.github.dumbgreenfish.dialogueforge.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.SettingsRepository
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class LlmService(
    private val settings: SettingsRepository,
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
    }

    suspend fun chat(
        systemPrompt: String,
        history: List<Pair<String, String>>,
        userMessage: String,
    ): Result<String> = runCatching {
        val endpoint = settings.getEndpoint()
        val model = settings.getModel()
        val temperature = settings.getTemperature()
        val maxTokens = settings.getMaxTokens()

        val messages = mutableListOf<ChatMessage>()
        if (systemPrompt.isNotBlank()) {
            messages.add(ChatMessage("system", systemPrompt))
        }
        for ((role, content) in history) {
            messages.add(ChatMessage(role, content))
        }
        messages.add(ChatMessage("user", userMessage))

        val request = ChatCompletionRequest(
            model = model,
            messages = messages,
            temperature = temperature,
            max_tokens = maxTokens,
        )

        val response: ChatCompletionResponse = client.post(endpoint) {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${settings.getApiKey()}")
            setBody(request)
        }.body()

        response.choices.firstOrNull()?.message?.content
            ?: throw IllegalStateException("No response from model")
    }
}
