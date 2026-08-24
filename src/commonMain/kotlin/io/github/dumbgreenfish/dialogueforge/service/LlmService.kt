package io.github.dumbgreenfish.dialogueforge.service

import io.github.dumbgreenfish.dialogueforge.data.repository.settings.SettingsRepository
import io.github.dumbgreenfish.dialogueforge.service.generation.GenerationLogger
import io.github.dumbgreenfish.dialogueforge.service.generation.logging.LlmGenerationTelemetry
import io.github.dumbgreenfish.dialogueforge.service.llm.LlmClient as CanonicalLlmClient
import io.github.dumbgreenfish.dialogueforge.service.llm.LlmService as CanonicalLlmService
import io.github.dumbgreenfish.dialogueforge.service.llm.request.ChatCompletionCallBuilder
import io.github.dumbgreenfish.dialogueforge.service.llm.response.BufferedCompletionReader
import io.github.dumbgreenfish.dialogueforge.service.llm.response.StreamingCompletionReader
import io.github.dumbgreenfish.dialogueforge.service.llm.transport.LlmHttpTransport
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.CancellationException

class LlmService private constructor(
    private val delegate: CanonicalLlmClient,
) : LlmClient {
    constructor(
        settings: SettingsRepository,
        logger: GenerationLogger,
    ) : this(createDelegate(settings, logger))

    internal constructor(
        settings: SettingsRepository,
        engine: HttpClientEngine,
        logger: GenerationLogger,
    ) : this(createDelegate(settings, logger, engine))

    override suspend fun chat(
        systemPrompt: String,
        history: List<Pair<String, String>>,
        onUpdate: (String) -> Unit,
    ): Result<String> = try {
        delegate.chat(systemPrompt, history) { response ->
            onUpdate(response)
        }
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        Result.failure(error)
    }

    private companion object {
        fun createDelegate(
            settings: SettingsRepository,
            logger: GenerationLogger,
            engine: HttpClientEngine? = null,
        ): CanonicalLlmClient = CanonicalLlmService(
            callBuilder = ChatCompletionCallBuilder(settings),
            transport = if (engine == null) LlmHttpTransport() else LlmHttpTransport(engine),
            bufferedReader = BufferedCompletionReader(),
            streamingReader = StreamingCompletionReader(),
            telemetry = LlmGenerationTelemetry(logger),
        )
    }
}
