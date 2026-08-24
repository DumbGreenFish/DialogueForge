package io.github.dumbgreenfish.dialogueforge.service.llm.transport

import io.github.dumbgreenfish.dialogueforge.data.dto.completion.ChatCompletionRequest
import io.github.dumbgreenfish.dialogueforge.service.generation.logging.GenerationMode
import io.github.dumbgreenfish.dialogueforge.service.llm.request.ChatCompletionCall
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpTimeoutCapability
import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LlmHttpTransportTest {
    @Test
    fun active_generation_has_no_total_or_between_chunk_timeout() = runBlocking {
        var observedTimeouts: HttpTimeoutConfig? = null
        val engine = MockEngine { request ->
            observedTimeouts = request.getCapabilityOrNull(HttpTimeoutCapability)
            respond(content = "ok", status = HttpStatusCode.OK)
        }
        val call = ChatCompletionCall(
            endpoint = "https://example.test/chat/completions",
            apiKey = "test-key",
            request = ChatCompletionRequest(model = "test-model", messages = emptyList()),
            responseContentType = ContentType.Application.Json,
            streaming = false,
            mode = GenerationMode.Buffered,
        )

        LlmHttpTransport(engine).execute(call) { Unit }

        val timeouts = assertNotNull(observedTimeouts)
        assertEquals(HttpTimeoutConfig.INFINITE_TIMEOUT_MS, timeouts.requestTimeoutMillis)
        assertEquals(CONNECTION_TIMEOUT_MILLIS, timeouts.connectTimeoutMillis)
        assertEquals(HttpTimeoutConfig.INFINITE_TIMEOUT_MS, timeouts.socketTimeoutMillis)
    }

    private companion object {
        const val CONNECTION_TIMEOUT_MILLIS = 300_000L
    }
}
