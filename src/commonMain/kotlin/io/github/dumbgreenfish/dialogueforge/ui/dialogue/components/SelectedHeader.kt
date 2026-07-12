package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components

import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_selection_count
import org.jetbrains.compose.resources.pluralStringResource

@Composable
internal fun SelectedHeader(
    selectedCount: Int,
    onClearSelection: (() -> Unit)?,
    onCopySelected: (() -> Unit)?,
    onDeleteSelected: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme

    DialogueHeaderRow(modifier = modifier) {
        IconButton(
            onClick = { onClearSelection?.invoke() },
            modifier = Modifier.requiredSize(DialogueHeaderDimens.ActionBtnTarget)
        ) {
            Icon(Icons.Filled.Close, contentDescription = null, tint = cs.onSurface)
        }
        Text(
            text = pluralStringResource(
                Res.plurals.dialogue_selection_count,
                selectedCount,
                selectedCount
            ),
            style = dialogueHeaderTitleStyle(),
            color = cs.onSurface,
            modifier = Modifier.weight(1f),
        )
        IconButton(
            onClick = { onCopySelected?.invoke() },
            modifier = Modifier.requiredSize(DialogueHeaderDimens.ActionBtnTarget)
        ) {
            Icon(
                Icons.Filled.ContentCopy,
                contentDescription = null,
                tint = cs.onSurfaceVariant
            )
        }
        IconButton(
            onClick = { onDeleteSelected?.invoke() },
            modifier = Modifier.requiredSize(DialogueHeaderDimens.ActionBtnTarget)
        ) {
            Icon(Icons.Filled.Delete, contentDescription = null, tint = cs.error)
        }
    }
}
