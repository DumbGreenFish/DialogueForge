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

    @Query("UPDATE conversations SET updated_at = :updatedAt WHERE id = :id")
    suspend fun touchConversation(id: String, updatedAt: Long)

    @Transaction
    suspend fun insertWithOrder(entity: MessageEntity): MessageEntity {
        val ordered = entity.copy(orderInConversation = countByConversation(entity.conversationId))
        insert(ordered)
        touchConversation(ordered.conversationId, ordered.timestamp)
        return ordered
    }
}
