package io.github.dumbgreenfish.dialogueforge.data.repository.character

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val creator: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val avatarData: ByteArray?,
    val tags: List<String>,
    @ColumnInfo(name = "spec_version") val specVersion: String,
    val pinned: Boolean,
    @ColumnInfo(name = "chat_count") val chatCount: Int,
    @ColumnInfo(name = "imported_at") val importedAt: Long,
    @ColumnInfo(name = "last_used_at") val lastUsedAt: Long?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CharacterEntity) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
