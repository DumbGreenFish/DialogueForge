package io.github.dumbgreenfish.dialogueforge.data.repository.character

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.TypeConverters

internal const val CHARACTER_DB_NAME = "characters.db"
internal const val CHARACTER_DB_DIR  = "db"

@Database(entities = [CharacterEntity::class], version = 1, exportSchema = false)
@TypeConverters(StringListConverter::class)
@ConstructedBy(CharacterDatabaseConstructor::class)
abstract class CharacterDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
}
