package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_selection_count
import org.jetbrains.compose.resources.pluralStringResource

private val HeaderHeight = 48.dp
private val HeaderPaddingH = 16.dp
private val HeaderGap = 12.dp
private val ActionBtnTarget = 36.dp

@Composable
internal fun SelectionHeader(
    selectedCount: Int,
    onClearSelection: () -> Unit,
    onCopySelected: () -> Unit,
    onDeleteSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(HeaderHeight)
            .background(cs.primaryContainer)
            .padding(horizontal = HeaderPaddingH),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HeaderGap),
    ) {
        IconButton(
            onClick = onClearSelection,
            modifier = Modifier.requiredSize(ActionBtnTarget),
        ) {
            Icon(Icons.Filled.Close, contentDescription = null, tint = cs.onPrimaryContainer)
        }
        Text(
            text = pluralStringResource(
                Res.plurals.dialogue_selection_count,
                selectedCount,
                selectedCount,
            ),
            style = MaterialTheme.typography.bodySmall,
            color = cs.onPrimaryContainer,
            modifier = Modifier.weight(1f),
        )
        IconButton(
            onClick = onCopySelected,
            modifier = Modifier.requiredSize(ActionBtnTarget),
        ) {
            Icon(
                Icons.Filled.ContentCopy,
                contentDescription = null,
                tint = cs.onPrimaryContainer.copy(alpha = 0.7f),
            )
        }
        IconButton(
            onClick = onDeleteSelected,
            modifier = Modifier.requiredSize(ActionBtnTarget),
        ) {
            Icon(Icons.Filled.Delete, contentDescription = null, tint = cs.error)
        }
    }
}
