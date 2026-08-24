package io.github.dumbgreenfish.dialogueforge.service.llm.request

import io.github.dumbgreenfish.dialogueforge.data.dto.completion.ChatMessage
import io.github.dumbgreenfish.dialogueforge.service.generation.logging.GenerationMode
import io.github.dumbgreenfish.dialogueforge.testing.FakeSettingsRepository
import io.ktor.http.ContentType
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ChatCompletionCallBuilderTest {
    @Test
    fun streaming_call_reads_all_settings_and_preserves_message_order() {
        runBlocking {
            val builder = ChatCompletionCallBuilder(
                FakeSettingsRepository(
                    apiKey = "secret-key",
                    endpoint = "https://example.test/completions",
                    model = "test-model",
                    temperature = 0.35f,
                    maxTokens = 1234,
                    streamResponses = true,
                ),
            )

            val call = builder.build(
                systemPrompt = "System prompt",
                history = listOf(
                    "assistant" to "Existing answer",
                    "user" to "New question",
                ),
            )

            assertEquals("https://example.test/completions", call.endpoint)
            assertEquals("secret-key", call.apiKey)
            assertEquals("test-model", call.request.model)
            assertEquals(0.35f, call.request.temperature)
            assertEquals(1234, call.request.maxTokens)
            assertEquals(
                listOf(
                    ChatMessage("system", "System prompt"),
                    ChatMessage("assistant", "Existing answer"),
                    ChatMessage("user", "New question"),
                ),
                call.request.messages,
            )
            assertTrue(call.streaming)
            assertTrue(call.request.stream)
            assertEquals(ContentType.Text.EventStream, call.responseContentType)
            assertEquals(GenerationMode.Streaming, call.mode)
        }
    }

    @Test
    fun buffered_call_omits_blank_system_prompt_and_selects_json_response() {
        runBlocking {
            val call = ChatCompletionCallBuilder(
                FakeSettingsRepository(streamResponses = false),
            ).build(
                systemPrompt = "   ",
                history = listOf("user" to "Question"),
            )

            assertEquals(listOf(ChatMessage("user", "Question")), call.request.messages)
            assertEquals(false, call.streaming)
            assertEquals(false, call.request.stream)
            assertEquals(ContentType.Application.Json, call.responseContentType)
            assertEquals(GenerationMode.Buffered, call.mode)
        }
    }

    @Test
    fun null_empty_and_blank_api_keys_are_rejected() {
        runBlocking {
            listOf<String?>(null, "", "   ").forEach { apiKey ->
                val error = try {
                    ChatCompletionCallBuilder(
                        FakeSettingsRepository(apiKey = apiKey),
                    ).build("", emptyList())
                    null
                } catch (error: Throwable) {
                    error
                }

                assertIs<MissingApiKeyException>(error)
                assertEquals("API key is not configured", error.message)
            }
        }
    }
}
