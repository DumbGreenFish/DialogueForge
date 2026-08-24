package io.github.dumbgreenfish.dialogueforge.service.generation.execution

import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.DialogueRepository
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatError
import org.koin.core.annotation.Single

internal interface GenerationSession {
    suspend fun begin(userText: String?): List<Pair<String, String>>
    suspend fun writeAssistantResponse(text: String)
    suspend fun complete(response: String)
    suspend fun fail(error: ChatError)
    suspend fun cancel()
    suspend fun interrupt()
}

@Single
open class ConversationGenerationSessionFactory(
    private val repository: DialogueRepository,
) {
    internal open fun create(conversationId: String): GenerationSession =
        ConversationGenerationSession(conversationId, repository)
}
