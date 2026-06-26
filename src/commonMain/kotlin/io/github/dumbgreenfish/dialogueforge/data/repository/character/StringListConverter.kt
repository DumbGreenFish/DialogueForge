package io.github.dumbgreenfish.dialogueforge.data.repository.character

import androidx.room3.ColumnTypeConverter
import kotlinx.serialization.json.Json

internal class StringListConverter {
    @ColumnTypeConverter
    fun fromList(value: List<String>): String = Json.encodeToString(value)

    @ColumnTypeConverter
    fun toList(value: String): List<String> = Json.decodeFromString(value)
}
