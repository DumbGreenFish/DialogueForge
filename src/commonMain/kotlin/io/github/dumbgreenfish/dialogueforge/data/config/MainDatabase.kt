package io.github.dumbgreenfish.dialogueforge.data.config

import androidx.room3.ColumnTypeConverters
import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterDao
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterEntity
import io.github.dumbgreenfish.dialogueforge.data.repository.character.StringListConverter
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.ConversationDao
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.ConversationEntity
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.MessageDao
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.MessageEntity

internal const val MAIN_DB_NAME = "characters.db"
internal const val MAIN_DB_DIR  = "db"

@Database(
    entities = [CharacterEntity::class, ConversationEntity::class, MessageEntity::class],
    version = 2,
    exportSchema = false,
)
@ColumnTypeConverters(StringListConverter::class)
@ConstructedBy(MainDatabaseConstructor::class)
abstract class MainDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
}
