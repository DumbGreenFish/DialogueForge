package io.github.dumbgreenfish.dialogueforge.data.repository.dialogue

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatErrorType

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "character_id") val characterId: String,
    val title: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    @ColumnInfo(name = "has_error") val hasError: Boolean = false,
    @ColumnInfo(name = "error_type") val errorType: ChatErrorType? = null,
    @ColumnInfo(name = "error_text") val errorText: String = "",
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ConversationEntity) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
