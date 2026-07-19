package io.github.dumbgreenfish.dialogueforge.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.SettingsRepository
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import kotlin.coroutines.cancellation.CancellationException

@Single
class LlmService(
    private val settings: SettingsRepository,
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 300_000
            connectTimeoutMillis = 300_000
            socketTimeoutMillis = 300_000
        }
    }

    suspend fun chat(
        systemPrompt: String,
        history: List<Pair<String, String>>,
    ): Result<String> = try {
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

        val request = ChatCompletionRequest(
            model = model,
            messages = messages,
            temperature = temperature,
            maxTokens = maxTokens,
        )
        val response: ChatCompletionResponse = client.post(endpoint) {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${settings.getApiKey()}")
            setBody(request)
        }.body()

        val content = response.choices.firstOrNull()?.message?.content
            ?: throw IllegalStateException("No response from model")
        Result.success(content)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
