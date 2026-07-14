package io.github.dumbgreenfish.dialogueforge.data.repository.dialogue

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages WHERE conversation_id = :conversationId ORDER BY order_in_conversation ASC")
    fun getByConversation(conversationId: String): Flow<List<MessageEntity>>

    @Query("SELECT COUNT(*) FROM messages WHERE conversation_id = :conversationId")
    suspend fun countByConversation(conversationId: String): Int

    @Query("SELECT * FROM messages WHERE conversation_id = :conversationId ORDER BY order_in_conversation DESC LIMIT :limit OFFSET :offset")
    suspend fun getByConversationPaged(conversationId: String, limit: Int, offset: Int): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getById(id: String): MessageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MessageEntity)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE messages SET text = :text WHERE id = :id")
    suspend fun updateText(id: String, text: String)

    @Query("UPDATE messages SET active_variant = :activeVariant, text = :text WHERE id = :id")
    suspend fun updateActiveVariant(id: String, activeVariant: Int, text: String)

    @Query("UPDATE conversations SET updated_at = :updatedAt WHERE id = :id")
    suspend fun touchConversation(id: String, updatedAt: Long)

    @Query("SELECT * FROM message_variants WHERE message_id = :messageId ORDER BY ordinal ASC")
    suspend fun getVariants(messageId: String): List<MessageVariantEntity>

    @Query("SELECT message_id, COUNT(*) AS variant_count FROM message_variants WHERE message_id IN (:messageIds) GROUP BY message_id")
    suspend fun countVariantsByMessageIds(messageIds: List<String>): List<MessageVariantCount>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariant(entity: MessageVariantEntity)

    @Query("DELETE FROM message_variants WHERE message_id = :messageId AND ordinal = :ordinal")
    suspend fun deleteVariant(messageId: String, ordinal: Int)

    @Transaction
    suspend fun insertWithOrder(entity: MessageEntity): MessageEntity {
        val ordered = entity.copy(orderInConversation = countByConversation(entity.conversationId))
        insert(ordered)
        touchConversation(ordered.conversationId, ordered.timestamp)
        return ordered
    }

    @Transaction
    suspend fun insertVariantAndActivate(messageId: String, variant: MessageVariantEntity) {
        insertVariant(variant)
        val text = variant.text
        updateActiveVariant(messageId, variant.ordinal, text)
    }
}
