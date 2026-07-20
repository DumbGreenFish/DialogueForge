package io.github.dumbgreenfish.dialogueforge.data.repository.dialogue

import androidx.room3.ColumnTypeConverter
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatErrorType

class ChatErrorTypeConverter {
    @ColumnTypeConverter
    fun fromChatErrorType(value: ChatErrorType): String = value.name

    @ColumnTypeConverter
    fun toChatErrorType(value: String): ChatErrorType = ChatErrorType.valueOf(value)
}
