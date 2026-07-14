package io.github.dumbgreenfish.dialogueforge.data.repository.dialogue

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "conversation_id") val conversationId: String,
    val role: String,
    val text: String,
    val timestamp: Long,
    @ColumnInfo(name = "order_in_conversation") val orderInConversation: Int,
    @ColumnInfo(name = "active_variant") val activeVariant: Int = 0,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MessageEntity) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
