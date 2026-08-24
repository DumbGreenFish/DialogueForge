package io.github.dumbgreenfish.dialogueforge.service.generation.execution

import io.github.dumbgreenfish.dialogueforge.data.dto.completion.LlmFinishReasonException
import io.github.dumbgreenfish.dialogueforge.data.dto.completion.LlmResponseException
import io.github.dumbgreenfish.dialogueforge.service.llm.request.MissingApiKeyException
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatError
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatErrorType
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ChatErrorMapperTest {
    @Test
    fun map_classifies_domain_and_unknown_errors_and_preserves_details() {
        val cases: List<Pair<Throwable, ChatErrorType>> = listOf(
            MissingApiKeyException() to ChatErrorType.NoApiKey,
            LlmFinishReasonException("length") to ChatErrorType.TokenLimit,
            LlmFinishReasonException("content_filter") to ChatErrorType.ContentFilter,
            LlmFinishReasonException("other") to ChatErrorType.Server,
            LlmResponseException(503, "Unavailable", "body") to ChatErrorType.Server,
            IllegalStateException("broken") to ChatErrorType.Unknown,
        )
        val mapper = ChatErrorMapper()

        cases.forEach { (error, expectedType) ->
            assertEquals(
                ChatError(expectedType, error.message.orEmpty()),
                mapper.map(error),
            )
        }
    }

    @Test
    fun map_classifies_real_ktor_client_and_server_errors() {
        runBlocking {
            val clientError = httpError(HttpStatusCode.BadRequest)
            val serverError = httpError(HttpStatusCode.ServiceUnavailable)
            assertIs<ClientRequestException>(clientError)
            assertIs<ServerResponseException>(serverError)

            val mapper = ChatErrorMapper()
            assertEquals(ChatErrorType.Server, mapper.map(clientError).type)
            assertEquals(ChatErrorType.Server, mapper.map(serverError).type)
        }
    }

    private suspend fun httpError(status: HttpStatusCode): Throwable {
        val client = HttpClient(
            MockEngine {
                respond(
                    content = "failure",
                    status = status,
                )
            },
        ) {
            expectSuccess = true
        }
        return try {
            try {
                client.get("https://example.test")
                error("Expected HTTP failure")
            } catch (error: Throwable) {
                error
            }
        } finally {
            client.close()
        }
    }
}
