package io.github.dumbgreenfish.dialogueforge.data.repository.settings

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query

@Dao
interface SettingDao {

    @Query("SELECT value FROM settings WHERE key = :key LIMIT 1")
    suspend fun get(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun set(entity: SettingEntity)
}
