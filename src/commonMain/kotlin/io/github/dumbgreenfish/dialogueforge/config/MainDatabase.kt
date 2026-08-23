package io.github.dumbgreenfish.dialogueforge.config

import androidx.room3.ColumnTypeConverters
import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterDao
import io.github.dumbgreenfish.dialogueforge.data.model.CharacterEntity
import io.github.dumbgreenfish.dialogueforge.util.converter.StringListConverter
import io.github.dumbgreenfish.dialogueforge.util.converter.ChatErrorTypeConverter
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.ConversationDao
import io.github.dumbgreenfish.dialogueforge.data.model.ConversationEntity
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.MessageDao
import io.github.dumbgreenfish.dialogueforge.data.model.MessageEntity
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.SettingDao
import io.github.dumbgreenfish.dialogueforge.data.model.SettingEntity

internal const val MAIN_DB_NAME = "characters.db"
internal const val MAIN_DB_DIR  = "db"

@Database(
    entities = [CharacterEntity::class, ConversationEntity::class, MessageEntity::class, SettingEntity::class],
    version = 8,
    exportSchema = false,
)
@ColumnTypeConverters(StringListConverter::class, ChatErrorTypeConverter::class)
@ConstructedBy(MainDatabaseConstructor::class)
abstract class MainDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun settingDao(): SettingDao
}
