package io.github.dumbgreenfish.dialogueforge.data.repository.dialogue

import kotlinx.coroutines.flow.Flow

data class ConversationResult(
    val conversation: ConversationEntity,
    val greetingMessageId: String?,
)

interface DialogueRepository {
    fun getMessages(conversationId: String): Flow<List<MessageEntity>>
    suspend fun getMessagesPage(conversationId: String, limit: Int, offset: Int): List<MessageEntity>
    suspend fun getMessageCount(conversationId: String): Int
    suspend fun getOrCreateConversation(characterId: String, greeting: String): ConversationResult
    suspend fun addMessage(conversationId: String, role: String, text: String): MessageEntity
    suspend fun deleteMessage(id: String)
    suspend fun updateMessage(id: String, text: String)
}
