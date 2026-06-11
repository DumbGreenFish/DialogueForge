package io.github.dumbgreenfish.dialogueforge.data.repository.character

import androidx.room3.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class StringListConverter {
    @TypeConverter
    fun fromList(value: List<String>): String = Json.encodeToString(value)

    @TypeConverter
    fun toList(value: String): List<String> = Json.decodeFromString(value)
}
