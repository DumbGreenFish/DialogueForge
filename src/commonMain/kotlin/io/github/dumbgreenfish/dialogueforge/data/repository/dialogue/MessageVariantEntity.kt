package io.github.dumbgreenfish.dialogueforge.data.repository.dialogue

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "message_variants",
    foreignKeys = [
        ForeignKey(
            entity = MessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["message_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["message_id"])],
)
data class MessageVariantEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "message_id") val messageId: String,
    val ordinal: Int,
    val text: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MessageVariantEntity) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
