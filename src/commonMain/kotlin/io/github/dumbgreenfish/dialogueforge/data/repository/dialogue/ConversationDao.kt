package io.github.dumbgreenfish.dialogueforge.data.repository.dialogue

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query

@Dao
interface ConversationDao {

    @Query("SELECT * FROM conversations WHERE character_id = :characterId ORDER BY updated_at DESC")
    suspend fun getByCharacterId(characterId: String): List<ConversationEntity>

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ConversationEntity)

    @Query("UPDATE conversations SET updated_at = :updatedAt WHERE id = :id")
    suspend fun touch(id: String, updatedAt: Long)
}
