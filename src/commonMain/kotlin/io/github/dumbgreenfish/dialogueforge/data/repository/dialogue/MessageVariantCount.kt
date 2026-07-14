package io.github.dumbgreenfish.dialogueforge.data.repository.dialogue

import androidx.room3.ColumnInfo

data class MessageVariantCount(
    @ColumnInfo(name = "message_id") val messageId: String,
    @ColumnInfo(name = "variant_count") val count: Int,
)
