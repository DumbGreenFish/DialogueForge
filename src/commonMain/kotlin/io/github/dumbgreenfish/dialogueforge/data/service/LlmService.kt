package io.github.dumbgreenfish.dialogueforge.data.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.SettingsRepository
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import kotlin.coroutines.cancellation.CancellationException

private const val MAX_ERROR_RESPONSE_BYTES = 8 * 1024

@Single
class LlmService(
    private val settings: SettingsRepository,
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private var injectedEngine: HttpClientEngine? = null

    internal constructor(settings: SettingsRepository, engine: HttpClientEngine) : this(settings) {
        injectedEngine = engine
    }

    private val client by lazy {
        val engine = injectedEngine
        if (engine == null) HttpClient { configureClient() } else HttpClient(engine) { configureClient() }
    }

    private fun io.ktor.client.HttpClientConfig<*>.configureClient() {
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
        val response = client.post(endpoint) {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${settings.getApiKey()}")
            setBody(request)
        }
        val responseBody = response.bodyAsText()
        val diagnosticBody = responseBody.truncatedForDiagnostic()

        if (response.status.value !in 200..299) {
            throw LlmResponseException(
                statusCode = response.status.value,
                statusDescription = response.status.description,
                responseBody = diagnosticBody,
            )
        }

        val completion = try {
            json.decodeFromString<ChatCompletionResponse>(responseBody)
        } catch (e: Exception) {
            throw LlmResponseException(
                statusCode = response.status.value,
                statusDescription = response.status.description,
                responseBody = diagnosticBody,
            )
        }

        val content = completion.choices.firstOrNull()?.message?.content?.takeIf { it.isNotBlank() }
            ?: throw LlmResponseException(
                statusCode = response.status.value,
                statusDescription = response.status.description,
                responseBody = diagnosticBody,
            )
        Result.success(content)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun String.truncatedForDiagnostic(): String {
        val bytes = encodeToByteArray()
        if (bytes.size <= MAX_ERROR_RESPONSE_BYTES) return this
        return bytes.decodeToString(
            startIndex = 0,
            endIndex = MAX_ERROR_RESPONSE_BYTES,
            throwOnInvalidSequence = false,
        ) + "\n…"
    }
}
