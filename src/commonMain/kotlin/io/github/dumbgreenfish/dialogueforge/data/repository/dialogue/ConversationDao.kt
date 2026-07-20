package io.github.dumbgreenfish.dialogueforge.data.repository.dialogue

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction

@Dao
interface ConversationDao {

    @Query("SELECT * FROM conversations WHERE character_id = :characterId ORDER BY updated_at DESC")
    suspend fun getByCharacterId(characterId: String): List<ConversationEntity>

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ConversationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(entity: MessageEntity)

    @Query("UPDATE conversations SET updated_at = :updatedAt WHERE id = :id")
    suspend fun touch(id: String, updatedAt: Long)

    @Query("UPDATE conversations SET has_error = 1, error_type = :errorType, error_text = :errorText WHERE id = :id")
    suspend fun setError(id: String, errorType: String, errorText: String)

    @Query("UPDATE conversations SET has_error = 0, error_type = NULL, error_text = '' WHERE id = :id")
    suspend fun clearError(id: String)

    @Transaction
    suspend fun getOrCreate(conversation: ConversationEntity, greeting: MessageEntity?): ConversationEntity {
        val existing = getByCharacterId(conversation.characterId).firstOrNull()
        if (existing != null) return existing

        insert(conversation)
        if (greeting != null) {
            insertMessage(greeting)
            touch(conversation.id, conversation.updatedAt)
        }
        return conversation
    }
}
