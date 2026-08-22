package io.github.dumbgreenfish.dialogueforge.data.service

import io.github.dumbgreenfish.dialogueforge.testing.FakeSettingsRepository
import io.github.dumbgreenfish.dialogueforge.testing.RecordingGenerationLogger
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.async
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LlmServiceTest {
    @Test
    fun standard_mode_preserves_buffered_json_request_and_response_contract() = runBlocking {
        val updates = mutableListOf<String>()
        val engine = MockEngine { request ->
            val requestJson = Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
            assertFalse("stream" in requestJson)
            assertEquals(ContentType.Application.Json, request.headers[HttpHeaders.Accept]?.let(ContentType::parse))
            respondJson(
                body = """{"choices":[{"message":{"role":"assistant","content":"Buffered answer"}}]}""",
                status = HttpStatusCode.OK,
            )
        }

        val result = service(
            engine = engine,
            settings = FakeSettingsRepository(streamResponses = false),
        ).chat("System", listOf("user" to "Hello"), updates::add)

        assertEquals("Buffered answer", result.getOrThrow())
        assertTrue(updates.isEmpty())
    }

    @Test
    fun standard_mode_logs_buffered_lifecycle_without_first_chunk_event() = runBlocking {
        val logger = RecordingGenerationLogger()
        val engine = MockEngine {
            respondJson(
                body = """{"choices":[{"message":{"role":"assistant","content":"Buffered answer"}}]}""",
                status = HttpStatusCode.OK,
            )
        }

        service(
            engine = engine,
            logger = logger,
            settings = FakeSettingsRepository(streamResponses = false),
        ).chat("", listOf("user" to "Hello")) {}.getOrThrow()

        assertEquals(listOf("started", "completed"), logger.entries.map { it.name })
        assertEquals(listOf("buffered", "buffered"), logger.entries.map { it.mode })
    }

    @Test
    fun streaming_mode_publishes_first_update_before_response_eof() = runBlocking {
        val responseChannel = ByteChannel(autoFlush = true)
        val releaseRemainder = CompletableDeferred<Unit>()
        val firstUpdate = CompletableDeferred<String>()
        val writer = launch {
            responseChannel.writeStringUtf8(textChunk("Visible now"))
            releaseRemainder.await()
            responseChannel.writeStringUtf8(finishChunk("stop") + doneEvent())
            responseChannel.close()
        }
        val engine = MockEngine { request ->
            val requestJson = Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
            assertTrue(requestJson.getValue("stream").jsonPrimitive.boolean)
            respond(
                content = responseChannel,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "text/event-stream"),
            )
        }

        val result = async {
            service(
                engine = engine,
                settings = FakeSettingsRepository(streamResponses = true),
            ).chat("", listOf("user" to "Hello")) { update ->
                firstUpdate.complete(update)
            }
        }

        assertEquals("Visible now", withTimeout(TEST_TIMEOUT_MILLIS) { firstUpdate.await() })
        assertFalse(result.isCompleted)
        releaseRemainder.complete(Unit)
        assertEquals("Visible now", withTimeout(TEST_TIMEOUT_MILLIS) { result.await().getOrThrow() })
        writer.join()
    }

    @Test
    fun streamed_response_sends_complete_request_and_reports_accumulated_text() = runBlocking {
        val updates = mutableListOf<String>()
        val engine = MockEngine { request ->
            assertEquals("https://example.test/chat/completions", request.url.toString())
            assertEquals("Bearer test-api-key", request.headers[HttpHeaders.Authorization])

            val requestJson = Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
            assertEquals("test-model", requestJson.getValue("model").jsonPrimitive.content)
            assertTrue(requestJson.getValue("stream").jsonPrimitive.boolean)
            val messages = requestJson.getValue("messages").jsonArray
            assertEquals(3, messages.size)
            assertEquals("system", messages[0].jsonObject.getValue("role").jsonPrimitive.content)
            assertEquals("You are RC001.", messages[0].jsonObject.getValue("content").jsonPrimitive.content)
            assertEquals("assistant", messages[1].jsonObject.getValue("role").jsonPrimitive.content)
            assertEquals("Hello", messages[1].jsonObject.getValue("content").jsonPrimitive.content)
            assertEquals("user", messages[2].jsonObject.getValue("role").jsonPrimitive.content)
            assertEquals("How old are you?", messages[2].jsonObject.getValue("content").jsonPrimitive.content)

            respondSse(
                roleChunk() +
                    textChunk("I am") +
                    textChunk(" a robot.") +
                    finishChunk("stop") +
                    doneEvent(),
            )
        }

        val result = service(engine).chat(
            systemPrompt = "You are RC001.",
            history = listOf(
                "assistant" to "Hello",
                "user" to "How old are you?",
            ),
            onUpdate = updates::add,
        )

        assertEquals("I am a robot.", result.getOrThrow())
        assertEquals(listOf("I am", "I am a robot."), updates)
    }

    @Test
    fun unicode_newlines_and_valid_non_text_chunks_are_processed_without_losing_content() = runBlocking {
        val updates = mutableListOf<String>()
        val body = roleChunk() +
            emptyChoicesChunk() +
            textChunk("Привет, ") +
            textChunk("мир!\n🤖") +
            usageChunk() +
            finishChunk("stop") +
            doneEvent()

        val result = service(MockEngine { respondSse(body) }).chat("", listOf("user" to "Hi"), updates::add)

        assertEquals("Привет, мир!\n🤖", result.getOrThrow())
        assertEquals(listOf("Привет, ", "Привет, мир!\n🤖"), updates)
    }

    @Test
    fun provider_error_inside_http_200_preserves_status_and_exact_event_data() = runBlocking {
        val data = """{"error":{"message":"Provider unavailable","type":"server_error"}}"""
        val result = service(MockEngine { respondSse(event(data) + doneEvent()) })
            .chat("", listOf("user" to "Hello")) {}

        val error = assertIs<LlmResponseException>(result.exceptionOrNull())
        assertEquals(200, error.statusCode)
        assertEquals(data, error.responseBody)
    }

    @Test
    fun malformed_chunk_after_text_keeps_last_update_and_preserves_bad_event() = runBlocking {
        val updates = mutableListOf<String>()
        val body = textChunk("Partial") + event("not-json") + doneEvent()

        val result = service(MockEngine { respondSse(body) })
            .chat("", listOf("user" to "Hello"), updates::add)

        val error = assertIs<LlmResponseException>(result.exceptionOrNull())
        assertEquals(listOf("Partial"), updates)
        assertEquals("not-json", error.responseBody)
    }

    @Test
    fun completed_stream_without_text_is_an_error() {
        runBlocking {
            val body = roleChunk() + emptyChoicesChunk() + finishChunk("stop") + doneEvent()

            val result = service(MockEngine { respondSse(body) }).chat("", listOf("user" to "Hello")) {}

            assertIs<LlmResponseException>(result.exceptionOrNull())
        }
    }

    @Test
    fun end_of_file_without_done_marker_is_an_error_after_preserving_partial_update() = runBlocking {
        val updates = mutableListOf<String>()
        val result = service(MockEngine { respondSse(textChunk("Partial") + finishChunk("stop")) })
            .chat("", listOf("user" to "Hello"), updates::add)

        val error = assertIs<LlmResponseException>(result.exceptionOrNull())
        assertEquals(listOf("Partial"), updates)
        assertTrue(error.message.orEmpty().contains("DONE"))
    }

    @Test
    fun length_finish_reason_fails_after_preserving_partial_text() = runBlocking {
        val updates = mutableListOf<String>()
        val body = textChunk("Truncated") + finishChunk("length") + doneEvent()

        val result = service(MockEngine { respondSse(body) })
            .chat("", listOf("user" to "Hello"), updates::add)

        val error = assertIs<LlmFinishReasonException>(result.exceptionOrNull())
        assertEquals("length", error.finishReason)
        assertEquals(listOf("Truncated"), updates)
    }

    @Test
    fun content_filter_finish_reason_fails_even_when_provider_returns_no_text() = runBlocking {
        val body = finishChunk("content_filter") + doneEvent()

        val result = service(MockEngine { respondSse(body) }).chat("", listOf("user" to "Hello")) {}

        val error = assertIs<LlmFinishReasonException>(result.exceptionOrNull())
        assertEquals("content_filter", error.finishReason)
    }

    @Test
    fun client_error_preserves_http_status_and_exact_body() = runBlocking {
        val body = """{"error":{"message":"Invalid request","type":"invalid_request_error"}}"""
        val error = failedHttpResponse(HttpStatusCode.BadRequest, body)

        assertEquals(400, error.statusCode)
        assertEquals("Bad Request", error.statusDescription)
        assertEquals(body, error.responseBody)
        assertEquals("HTTP 400 Bad Request\n$body", error.message)
    }

    @Test
    fun response_body_over_eight_kib_is_truncated_with_marker() = runBlocking {
        val body = "x".repeat(8 * 1024 + 1)
        val error = failedHttpResponse(HttpStatusCode.ServiceUnavailable, body)

        assertEquals("x".repeat(8 * 1024) + "\n…", error.responseBody)
    }

    @Test
    fun cancellation_is_rethrown_and_logged_without_becoming_a_result_failure() = runBlocking {
        val requestStarted = CompletableDeferred<Unit>()
        val neverCompletes = CompletableDeferred<Unit>()
        val logger = RecordingGenerationLogger()
        val engine = MockEngine {
            requestStarted.complete(Unit)
            neverCompletes.await()
            respondSse(doneEvent())
        }
        val job = launch {
            service(engine, logger).chat("", listOf("user" to "Hello")) {}
        }

        withTimeout(TEST_TIMEOUT_MILLIS) { requestStarted.await() }
        job.cancel()
        job.join()

        assertEquals(listOf("started", "cancelled"), logger.entries.map { it.name })
    }

    @Test
    fun lifecycle_logs_contain_aggregate_metadata_but_no_credentials_or_conversation_content() = runBlocking {
        val logger = RecordingGenerationLogger()
        val secretSettings = FakeSettingsRepository(
            apiKey = "super-secret-key",
            endpoint = "https://example.test/chat/completions?private=query-secret",
            model = "private-model-name",
            streamResponses = true,
        )
        val body = textChunk("Sensitive generated answer") + finishChunk("stop") + doneEvent()
        val service = LlmService(secretSettings, MockEngine { respondSse(body) }, logger)

        assertEquals(
            "Sensitive generated answer",
            service.chat("Sensitive system prompt", listOf("user" to "Sensitive user prompt")) {}.getOrThrow(),
        )

        assertEquals(listOf("started", "first_chunk", "completed"), logger.entries.map { it.name })
        assertEquals(listOf("streaming", "streaming", "streaming"), logger.entries.map { it.mode })
        val completed = logger.entries.last()
        assertEquals(1, completed.chunkCount)
        assertEquals("Sensitive generated answer".length, completed.characterCount)
        assertEquals("stop", completed.finishReason)
        val rendered = logger.entries.joinToString()
        assertTrue("super-secret-key" !in rendered)
        assertTrue("query-secret" !in rendered)
        assertTrue("private-model-name" !in rendered)
        assertTrue("Sensitive system prompt" !in rendered)
        assertTrue("Sensitive user prompt" !in rendered)
        assertTrue("Sensitive generated answer" !in rendered)
        assertNull(logger.entries.last().errorType)
    }

    private fun service(
        engine: MockEngine,
        logger: RecordingGenerationLogger = RecordingGenerationLogger(),
        settings: FakeSettingsRepository = FakeSettingsRepository(streamResponses = true),
    ) = LlmService(settings, engine, logger)

    private suspend fun failedHttpResponse(status: HttpStatusCode, body: String): LlmResponseException {
        val engine = MockEngine { respondJson(body, status) }
        val result = service(engine).chat("", listOf("user" to "Hello")) {}
        return assertIs<LlmResponseException>(result.exceptionOrNull())
    }

    private fun io.ktor.client.engine.mock.MockRequestHandleScope.respondSse(body: String) = respond(
        content = body,
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "text/event-stream"),
    )

    private fun io.ktor.client.engine.mock.MockRequestHandleScope.respondJson(
        body: String,
        status: HttpStatusCode,
    ) = respond(
        content = body,
        status = status,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
    )

    private companion object {
        const val TEST_TIMEOUT_MILLIS = 5_000L

        fun event(data: String) = "data: $data\n\n"
        fun doneEvent() = event("[DONE]")
        fun roleChunk() = event("""{"choices":[{"index":0,"delta":{"role":"assistant"},"finish_reason":null}]}""")
        fun emptyChoicesChunk() = event("""{"choices":[]}""")
        fun usageChunk() = event("""{"choices":[],"usage":{"prompt_tokens":1,"completion_tokens":2,"total_tokens":3}}""")
        fun finishChunk(reason: String) =
            event("""{"choices":[{"index":0,"delta":{},"finish_reason":"$reason"}]}""")

        fun textChunk(text: String): String {
            val encoded = Json.encodeToString(text)
            return event("""{"choices":[{"index":0,"delta":{"content":$encoded},"finish_reason":null}]}""")
        }
    }
}
