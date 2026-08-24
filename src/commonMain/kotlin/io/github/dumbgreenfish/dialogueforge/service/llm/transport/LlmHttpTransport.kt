package io.github.dumbgreenfish.dialogueforge.service.llm.transport

import io.github.dumbgreenfish.dialogueforge.data.dto.completion.LlmResponseException
import io.github.dumbgreenfish.dialogueforge.service.llm.request.ChatCompletionCall
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

private const val MAX_ERROR_RESPONSE_BYTES = 8 * 1024
private const val LLM_CONNECTION_TIMEOUT_MILLIS = 300_000L

@Single
class LlmHttpTransport() {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    private var injectedEngine: HttpClientEngine? = null

    internal constructor(engine: HttpClientEngine) : this() {
        injectedEngine = engine
    }

    private val client by lazy {
        val engine = injectedEngine
        if (engine == null) {
            HttpClient { configureClient() }
        } else {
            HttpClient(engine) { configureClient() }
        }
    }

    suspend fun <T> execute(
        call: ChatCompletionCall,
        readResponse: suspend (HttpResponse) -> T,
    ): T = client.preparePost(call.endpoint) {
        contentType(ContentType.Application.Json)
        accept(call.responseContentType)
        header("Authorization", "Bearer ${call.apiKey}")
        setBody(call.request)
    }.execute { response ->
        validateResponse(response)
        readResponse(response)
    }

    private fun HttpClientConfig<*>.configureClient() {
        install(ContentNegotiation) {
            json(json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
            connectTimeoutMillis = LLM_CONNECTION_TIMEOUT_MILLIS
            socketTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
        }
    }

    private suspend fun validateResponse(response: HttpResponse) {
        if (response.status.value in 200..299) return

        throw LlmResponseException(
            statusCode = response.status.value,
            statusDescription = response.status.description,
            responseBody = response.bodyAsText().truncatedForDiagnostic(),
        )
    }
}

internal fun String.truncatedForDiagnostic(): String {
    val bytes = encodeToByteArray()
    if (bytes.size <= MAX_ERROR_RESPONSE_BYTES) return this

    return bytes.decodeToString(
        startIndex = 0,
        endIndex = MAX_ERROR_RESPONSE_BYTES,
        throwOnInvalidSequence = false,
    ) + "\n…"
}
