package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.bubble

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val ActionIconGap = 8.dp
private val EditIconGap = 4.dp

@Composable
internal fun MessageActionRow(
    isEditing: Boolean,
    actionRowVisible: Boolean,
    messageId: String,
    onSave: ((String) -> Unit)?,
    onCancel: (() -> Unit)?,
    onCopy: ((String) -> Unit)?,
    onEdit: ((String) -> Unit)?,
    onDelete: ((String) -> Unit)?,
) {
    if (isEditing) {
        InlineActionIcon(
            icon = Icons.Filled.Check,
            onClick = { onSave?.invoke(messageId) },
            visible = true,
        )
        Spacer(Modifier.size(EditIconGap))
        InlineActionIcon(
            icon = Icons.Filled.Close,
            onClick = { onCancel?.invoke() },
            visible = true,
        )
    } else {
        InlineActionIcon(
            icon = Icons.Filled.ContentCopy,
            onClick = { onCopy?.invoke(messageId) },
            visible = actionRowVisible,
        )
        Spacer(Modifier.size(ActionIconGap))
        InlineActionIcon(
            icon = Icons.Filled.Edit,
            onClick = { onEdit?.invoke(messageId) },
            visible = actionRowVisible,
        )
        Spacer(Modifier.size(ActionIconGap))
        InlineActionIcon(
            icon = Icons.Filled.Delete,
            onClick = { onDelete?.invoke(messageId) },
            visible = actionRowVisible,
        )
    }
}
