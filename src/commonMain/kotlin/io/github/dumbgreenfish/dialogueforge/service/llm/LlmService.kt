package io.github.dumbgreenfish.dialogueforge.service.llm

import io.github.dumbgreenfish.dialogueforge.service.generation.logging.LlmGenerationTelemetry
import io.github.dumbgreenfish.dialogueforge.service.llm.request.ChatCompletionCallBuilder
import io.github.dumbgreenfish.dialogueforge.service.llm.response.BufferedCompletionReader
import io.github.dumbgreenfish.dialogueforge.service.llm.response.StreamingCompletionReader
import io.github.dumbgreenfish.dialogueforge.service.llm.transport.LlmHttpTransport
import org.koin.core.annotation.Single

@Single(binds = [LlmClient::class])
class LlmService(
    private val callBuilder: ChatCompletionCallBuilder,
    private val transport: LlmHttpTransport,
    private val bufferedReader: BufferedCompletionReader,
    private val streamingReader: StreamingCompletionReader,
    private val telemetry: LlmGenerationTelemetry,
) : LlmClient {
    override suspend fun chat(
        systemPrompt: String,
        history: List<Pair<String, String>>,
        onUpdate: suspend (String) -> Unit,
    ): Result<String> {
        val call = callBuilder.build(systemPrompt, history)

        return telemetry.execute(call.mode, onUpdate) { trackedUpdate ->
            transport.execute(call) { response ->
                if (call.streaming) {
                    streamingReader.read(response, trackedUpdate)
                } else {
                    bufferedReader.read(response)
                }
            }
        }
    }
}
