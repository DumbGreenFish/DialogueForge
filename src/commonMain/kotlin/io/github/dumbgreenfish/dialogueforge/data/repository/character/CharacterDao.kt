package io.github.dumbgreenfish.dialogueforge.data.repository.character

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters ORDER BY pinned DESC, imported_at DESC")
    fun getAllFlow(): Flow<List<CharacterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CharacterEntity)

    @Update
    suspend fun update(entity: CharacterEntity)

    @Query("DELETE FROM characters WHERE id = :id")
    suspend fun delete(id: String)

    @Query("UPDATE characters SET pinned = NOT pinned WHERE id = :id")
    suspend fun togglePin(id: String)

    @Query("SELECT * FROM characters WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): CharacterEntity?

    @Query("SELECT thumbnail_data FROM characters WHERE id = :id LIMIT 1")
    suspend fun getThumbnailData(id: String): ByteArray?

    @Query("SELECT avatarData FROM characters WHERE id = :id LIMIT 1")
    suspend fun getFullImageData(id: String): ByteArray?

    @Query("SELECT thumb_s FROM characters WHERE id = :id LIMIT 1")
    suspend fun getThumbnailSmall(id: String): ByteArray?

    @Query("SELECT thumb_m FROM characters WHERE id = :id LIMIT 1")
    suspend fun getThumbnailMedium(id: String): ByteArray?

    @Query("SELECT thumb_l FROM characters WHERE id = :id LIMIT 1")
    suspend fun getThumbnailLarge(id: String): ByteArray?
}
