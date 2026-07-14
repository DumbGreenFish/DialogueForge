package io.github.dumbgreenfish.dialogueforge.data.repository.dialogue

import kotlinx.coroutines.flow.Flow

interface DialogueRepository {
    fun getMessages(conversationId: String): Flow<List<MessageEntity>>
    suspend fun getMessagesPage(conversationId: String, limit: Int, offset: Int): List<MessageEntity>
    suspend fun getMessageCount(conversationId: String): Int
    suspend fun getOrCreateConversation(characterId: String, greeting: String): ConversationEntity
    suspend fun addMessage(conversationId: String, role: String, text: String): MessageEntity
    suspend fun deleteMessage(id: String)
    suspend fun updateMessage(id: String, text: String)
    suspend fun getVariants(messageId: String): List<MessageVariantEntity>
    suspend fun addVariant(messageId: String, text: String): MessageVariantEntity
    suspend fun setActiveVariant(messageId: String, ordinal: Int): MessageEntity?
    suspend fun countVariantsByMessageIds(messageIds: List<String>): List<MessageVariantCount>
}
