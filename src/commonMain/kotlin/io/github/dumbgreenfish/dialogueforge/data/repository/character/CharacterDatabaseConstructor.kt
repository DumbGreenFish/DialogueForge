package io.github.dumbgreenfish.dialogueforge.data.repository.character

import androidx.room3.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object CharacterDatabaseConstructor : RoomDatabaseConstructor<CharacterDatabase> {
    override fun initialize(): CharacterDatabase
}
