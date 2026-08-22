package io.github.dumbgreenfish.dialogueforge.data.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.readUTF8Line
import io.github.dumbgreenfish.dialogueforge.data.generation.GenerationLogger
import io.github.dumbgreenfish.dialogueforge.data.generation.GenerationMode
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.SettingsRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.koin.core.annotation.Single
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.TimeSource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val MAX_ERROR_RESPONSE_BYTES = 8 * 1024

@Single(binds = [LlmClient::class])
@OptIn(ExperimentalUuidApi::class)
class LlmService(
    private val settings: SettingsRepository,
    private val logger: GenerationLogger,
) : LlmClient {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private var injectedEngine: HttpClientEngine? = null

    internal constructor(
        settings: SettingsRepository,
        engine: HttpClientEngine,
        logger: GenerationLogger,
    ) : this(settings, logger) {
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

    override suspend fun chat(
        systemPrompt: String,
        history: List<Pair<String, String>>,
        onUpdate: (String) -> Unit,
    ): Result<String> {
        val requestId = Uuid.random().toString()
        val startedAt = TimeSource.Monotonic.markNow()
        var chunkCount = 0
        var characterCount = 0
        var finishReason: String? = null
        var mode = GenerationMode.Buffered
        return try {
            val endpoint = settings.getEndpoint()
            val model = settings.getModel()
            val temperature = settings.getTemperature()
            val maxTokens = settings.getMaxTokens()
            val streamResponses = settings.getStreamResponses()
            mode = if (streamResponses) GenerationMode.Streaming else GenerationMode.Buffered
            logger.generationStarted(requestId, mode)

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
                stream = streamResponses,
            )
            val apiKey = settings.getApiKey()

            val content = if (streamResponses) {
                client.preparePost(endpoint) {
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Text.EventStream)
                    header("Authorization", "Bearer $apiKey")
                    setBody(request)
                }.execute { response ->
                    validateResponse(response)

                    val accumulated = StringBuilder()
                    var done = false
                    val dataLines = mutableListOf<String>()

                    suspend fun processEvent() {
                        if (dataLines.isEmpty()) return
                        val eventData = dataLines.joinToString("\n")
                        dataLines.clear()
                        if (eventData == "[DONE]") {
                            done = true
                            return
                        }
                        val chunk = try {
                            val element = json.parseToJsonElement(eventData)
                            if ("error" in element.jsonObject) {
                                throw IllegalStateException("Provider returned an error event")
                            }
                            json.decodeFromJsonElement(ChatCompletionChunk.serializer(), element)
                        } catch (_: Exception) {
                            throw LlmResponseException(
                                statusCode = response.status.value,
                                statusDescription = response.status.description,
                                responseBody = eventData.truncatedForDiagnostic(),
                            )
                        }
                        chunk.choices.firstOrNull()?.let { choice ->
                            choice.finishReason?.let { finishReason = it }
                            choice.delta.content?.takeIf(String::isNotEmpty)?.let { delta ->
                                accumulated.append(delta)
                                chunkCount += 1
                                characterCount = accumulated.length
                                if (chunkCount == 1) {
                                    logger.firstChunk(
                                        requestId,
                                        mode,
                                        startedAt.elapsedNow().inWholeMilliseconds,
                                    )
                                }
                                onUpdate(accumulated.toString())
                            }
                        }
                    }

                    val channel = response.bodyAsChannel()
                    while (!channel.isClosedForRead) {
                        val line = channel.readUTF8Line() ?: break
                        when {
                            line.isEmpty() -> processEvent()
                            line.startsWith("data:") -> dataLines += line.removePrefix("data:").removePrefix(" ")
                        }
                    }
                    processEvent()

                    if (!done) {
                        throw LlmResponseException(
                            statusCode = response.status.value,
                            statusDescription = response.status.description,
                            responseBody = "Stream ended without [DONE] marker",
                        )
                    }
                    if (finishReason == "length" || finishReason == "content_filter") {
                        throw LlmFinishReasonException(checkNotNull(finishReason))
                    }
                    accumulated.toString().takeIf { it.isNotBlank() }
                        ?: throw LlmResponseException(
                            statusCode = response.status.value,
                            statusDescription = response.status.description,
                            responseBody = "Stream completed without text content",
                        )
                }
            } else {
                val response = client.post(endpoint) {
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                    header("Authorization", "Bearer $apiKey")
                    setBody(request)
                }
                validateResponse(response)
                val responseBody = response.bodyAsText()
                val completion = try {
                    json.decodeFromString<ChatCompletionResponse>(responseBody)
                } catch (_: Exception) {
                    throw LlmResponseException(
                        statusCode = response.status.value,
                        statusDescription = response.status.description,
                        responseBody = responseBody.truncatedForDiagnostic(),
                    )
                }
                val choice = completion.choices.firstOrNull()
                finishReason = choice?.finishReason
                if (finishReason == "length" || finishReason == "content_filter") {
                    throw LlmFinishReasonException(checkNotNull(finishReason))
                }
                choice?.message?.content?.takeIf(String::isNotBlank)
                    ?: throw LlmResponseException(
                        statusCode = response.status.value,
                        statusDescription = response.status.description,
                        responseBody = responseBody.truncatedForDiagnostic(),
                    )
            }
            characterCount = content.length
            logger.generationCompleted(
                requestId,
                mode,
                chunkCount,
                characterCount,
                finishReason,
                startedAt.elapsedNow().inWholeMilliseconds,
            )
            Result.success(content)
        } catch (e: CancellationException) {
            logger.generationCancelled(
                requestId,
                mode,
                chunkCount,
                characterCount,
                startedAt.elapsedNow().inWholeMilliseconds,
            )
            throw e
        } catch (e: Exception) {
            logger.generationFailed(
                requestId,
                mode,
                chunkCount,
                characterCount,
                startedAt.elapsedNow().inWholeMilliseconds,
                e,
            )
            Result.failure(e)
        }
    }

    private suspend fun validateResponse(response: io.ktor.client.statement.HttpResponse) {
        if (response.status.value !in 200..299) {
            val responseBody = response.bodyAsText()
            throw LlmResponseException(
                statusCode = response.status.value,
                statusDescription = response.status.description,
                responseBody = responseBody.truncatedForDiagnostic(),
            )
        }
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
