package io.github.dumbgreenfish.dialogueforge.data.service

import io.github.dumbgreenfish.dialogueforge.testing.FakeSettingsRepository
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LlmServiceTest {
    @Test
    fun successful_response_returns_content_and_sends_complete_history() = runBlocking {
        val engine = MockEngine { request ->
            assertEquals("https://example.test/chat/completions", request.url.toString())
            assertEquals("Bearer test-api-key", request.headers[HttpHeaders.Authorization])

            val requestJson = Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
            assertEquals("test-model", requestJson.getValue("model").jsonPrimitive.content)
            val messages = requestJson.getValue("messages").jsonArray
            assertEquals(3, messages.size)
            assertEquals("system", messages[0].jsonObject.getValue("role").jsonPrimitive.content)
            assertEquals("You are RC001.", messages[0].jsonObject.getValue("content").jsonPrimitive.content)
            assertEquals("assistant", messages[1].jsonObject.getValue("role").jsonPrimitive.content)
            assertEquals("Hello", messages[1].jsonObject.getValue("content").jsonPrimitive.content)
            assertEquals("user", messages[2].jsonObject.getValue("role").jsonPrimitive.content)
            assertEquals("How old are you?", messages[2].jsonObject.getValue("content").jsonPrimitive.content)

            respondJson(
                body = """{"choices":[{"message":{"role":"assistant","content":"I am a robot."}}]}""",
                status = HttpStatusCode.OK,
            )
        }

        val result = LlmService(FakeSettingsRepository(), engine).chat(
            systemPrompt = "You are RC001.",
            history = listOf(
                "assistant" to "Hello",
                "user" to "How old are you?",
            ),
        )

        assertEquals("I am a robot.", result.getOrThrow())
    }

    @Test
    fun content_filter_response_preserves_http_200_and_exact_body() = runBlocking {
        val body = """{"choices":[{"finish_reason":"content_filter: PROHIBITED_CONTENT","index":0}],"model":"gemini-3.6-flash"}"""
        val error = failedResponse(HttpStatusCode.OK, body)

        assertEquals(200, error.statusCode)
        assertEquals("OK", error.statusDescription)
        assertEquals(body, error.responseBody)
        assertEquals("HTTP 200 OK\n$body", error.message)
    }

    @Test
    fun client_error_preserves_http_status_and_exact_body() = runBlocking {
        val body = """{"error":{"message":"Invalid request","type":"invalid_request_error"}}"""
        val error = failedResponse(HttpStatusCode.BadRequest, body)

        assertEquals(400, error.statusCode)
        assertEquals("Bad Request", error.statusDescription)
        assertEquals(body, error.responseBody)
        assertEquals("HTTP 400 Bad Request\n$body", error.message)
    }

    @Test
    fun server_error_preserves_http_status_and_exact_body() = runBlocking {
        val body = "upstream unavailable"
        val error = failedResponse(HttpStatusCode.ServiceUnavailable, body)

        assertEquals(503, error.statusCode)
        assertEquals("Service Unavailable", error.statusDescription)
        assertEquals(body, error.responseBody)
    }

    @Test
    fun malformed_success_response_preserves_http_status_and_exact_body() = runBlocking {
        val body = "not-json"
        val error = failedResponse(HttpStatusCode.OK, body)

        assertEquals(200, error.statusCode)
        assertEquals(body, error.responseBody)
    }

    @Test
    fun blank_assistant_content_preserves_http_status_and_exact_body() = runBlocking {
        val body = """{"choices":[{"message":{"role":"assistant","content":"   "}}]}"""
        val error = failedResponse(HttpStatusCode.OK, body)

        assertEquals(200, error.statusCode)
        assertEquals(body, error.responseBody)
    }

    @Test
    fun response_body_at_eight_kib_boundary_is_not_truncated() = runBlocking {
        val body = "x".repeat(8 * 1024)
        val error = failedResponse(HttpStatusCode.BadRequest, body)

        assertEquals(body, error.responseBody)
    }

    @Test
    fun response_body_over_eight_kib_is_truncated_with_marker() = runBlocking {
        val body = "x".repeat(8 * 1024 + 1)
        val error = failedResponse(HttpStatusCode.BadRequest, body)

        assertEquals("x".repeat(8 * 1024) + "\n…", error.responseBody)
        assertTrue(error.responseBody.encodeToByteArray().size > 8 * 1024)
    }

    private suspend fun failedResponse(status: HttpStatusCode, body: String): LlmResponseException {
        val engine = MockEngine {
            respondJson(body = body, status = status)
        }
        val result = LlmService(FakeSettingsRepository(), engine).chat(
            systemPrompt = "",
            history = listOf("user" to "Hello"),
        )
        return assertIs<LlmResponseException>(result.exceptionOrNull())
    }

    private fun io.ktor.client.engine.mock.MockRequestHandleScope.respondJson(
        body: String,
        status: HttpStatusCode,
    ) = respond(
        content = body,
        status = status,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
    )
}
