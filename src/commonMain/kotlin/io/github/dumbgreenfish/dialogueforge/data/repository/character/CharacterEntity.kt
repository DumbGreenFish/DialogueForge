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
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val avatarData: ByteArray,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB, name = "thumbnail_data") val mainImageThumbnailData: ByteArray,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB, name = "thumb_s") val thumbnailSmall: ByteArray,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB, name = "thumb_m") val thumbnailMedium: ByteArray,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB, name = "thumb_l") val thumbnailLarge: ByteArray,
    val tags: List<String>,
    @ColumnInfo(name = "spec_version") val specVersion: String,
    val pinned: Boolean,
    @ColumnInfo(name = "chat_count") val chatCount: Int,
    @ColumnInfo(name = "imported_at") val importedAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    @ColumnInfo(name = "last_used_at") val lastUsedAt: Long?,
    @ColumnInfo(name = "first_message") val firstMessage: String = "",
    @ColumnInfo(name = "personality") val personality: String = "",
    @ColumnInfo(name = "scenario") val scenario: String = "",
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CharacterEntity) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
