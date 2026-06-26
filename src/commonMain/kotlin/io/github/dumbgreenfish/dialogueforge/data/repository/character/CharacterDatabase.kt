package io.github.dumbgreenfish.dialogueforge.data.repository.character

import androidx.room3.ColumnTypeConverters
import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase

internal const val CHARACTER_DB_NAME = "characters.db"
internal const val CHARACTER_DB_DIR  = "db"

@Database(entities = [CharacterEntity::class], version = 1, exportSchema = false)
@ColumnTypeConverters(StringListConverter::class)
@ConstructedBy(CharacterDatabaseConstructor::class)
abstract class CharacterDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
}
