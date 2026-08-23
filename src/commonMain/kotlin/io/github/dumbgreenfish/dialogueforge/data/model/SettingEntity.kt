package io.github.dumbgreenfish.dialogueforge.data.model

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey val key: String,
    @ColumnInfo(name = "value") val value: String,
)