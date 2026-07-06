package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.bubble

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val TimestampTop = 8.dp
private val CheckmarkSize = 12.dp
private val TimestampCheckGap = 4.dp

@Composable
internal fun MessageBubbleFooter(
    isUser: Boolean,
    isEditing: Boolean,
    actionRowVisible: Boolean,
    messageId: String,
    timestampText: String,
    timestampColor: Color,
    onSave: ((String) -> Unit)?,
    onCancel: (() -> Unit)?,
    onCopy: ((String) -> Unit)?,
    onEdit: ((String) -> Unit)?,
    onDelete: ((String) -> Unit)?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = TimestampTop),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MessageActionRow(
            isEditing = isEditing,
            actionRowVisible = actionRowVisible,
            messageId = messageId,
            onSave = onSave,
            onCancel = onCancel,
            onCopy = onCopy,
            onEdit = onEdit,
            onDelete = onDelete,
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = timestampText,
            color = timestampColor,
            style = MaterialTheme.typography.labelSmall,
        )
        if (isUser) {
            Spacer(Modifier.size(TimestampCheckGap))
            Icon(
                Icons.Filled.DoneAll,
                contentDescription = null,
                modifier = Modifier.size(CheckmarkSize),
                tint = timestampColor,
            )
        }
    }
}
