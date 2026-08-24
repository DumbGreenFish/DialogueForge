package io.github.dumbgreenfish.dialogueforge.service.generation.execution

import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.DialogueRepository
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatError
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatErrorType
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

internal class ConversationGenerationSession(
    private val conversationId: String,
    private val repository: DialogueRepository,
) : GenerationSession {
    private var assistantMessageId: String? = null

    override suspend fun begin(userText: String?): List<Pair<String, String>> {
        repository.clearConversationError(conversationId)
        if (userText != null) {
            repository.addMessage(
                conversationId = conversationId,
                role = USER_ROLE,
                text = userText,
            )
        }
        repository.setConversationError(
            conversationId = conversationId,
            errorType = ChatErrorType.Interrupted.name,
            errorText = "",
        )
        return repository
            .getMessageHistory(conversationId)
            .map { message -> message.role to message.text }
    }

    override suspend fun writeAssistantResponse(text: String) {
        withContext(NonCancellable) {
            val messageId = assistantMessageId
            if (messageId == null) {
                assistantMessageId = repository.addMessage(
                    conversationId = conversationId,
                    role = ASSISTANT_ROLE,
                    text = text,
                ).id
            } else {
                repository.updateMessage(
                    id = messageId,
                    text = text,
                )
            }
        }
    }

    override suspend fun complete(response: String) {
        writeAssistantResponse(response)
        repository.clearConversationError(conversationId)
    }

    override suspend fun fail(error: ChatError) {
        repository.setConversationError(
            conversationId = conversationId,
            errorType = error.type.name,
            errorText = error.details,
        )
    }

    override suspend fun cancel() {
        withContext(NonCancellable) {
            repository.clearConversationError(conversationId)
        }
    }

    override suspend fun interrupt() = Unit

    private companion object {
        const val ASSISTANT_ROLE = "assistant"
        const val USER_ROLE = "user"
    }
}
