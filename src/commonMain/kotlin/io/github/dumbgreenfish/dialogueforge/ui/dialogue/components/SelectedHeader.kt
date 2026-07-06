package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components

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
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass
import org.jetbrains.compose.resources.pluralStringResource

private val HeightCompact = 64.dp
private val HeightWide = 56.dp
private val PaddingHCompact = 4.dp
private val PaddingHWide = 12.dp
private val GapCompact = 0.dp
private val GapWide = 4.dp
private val ActionBtnTarget = 36.dp

@Composable
internal fun SelectedHeader(
    selectedCount: Int,
    onClearSelection: (() -> Unit)?,
    onCopySelected: (() -> Unit)?,
    onDeleteSelected: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    val compact = windowClass != WindowClass.Wide

    val height = if (compact) HeightCompact else HeightWide
    val paddingH = if (compact) PaddingHCompact else PaddingHWide
    val gap = if (compact) GapCompact else GapWide
    val titleStyle = if (compact) MaterialTheme.typography.titleMedium
    else MaterialTheme.typography.titleSmall

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .padding(horizontal = paddingH),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(gap),
    ) {
        IconButton(
            onClick = { onClearSelection?.invoke() },
            modifier = Modifier.requiredSize(ActionBtnTarget)
        ) {
            Icon(Icons.Filled.Close, contentDescription = null, tint = cs.onSurface)
        }
        Text(
            text = pluralStringResource(
                Res.plurals.dialogue_selection_count,
                selectedCount,
                selectedCount
            ),
            style = titleStyle,
            color = cs.onSurface,
            modifier = Modifier.weight(1f),
        )
        IconButton(
            onClick = { onCopySelected?.invoke() },
            modifier = Modifier.requiredSize(ActionBtnTarget)
        ) {
            Icon(
                Icons.Filled.ContentCopy,
                contentDescription = null,
                tint = cs.onSurfaceVariant
            )
        }
        IconButton(
            onClick = { onDeleteSelected?.invoke() },
            modifier = Modifier.requiredSize(ActionBtnTarget)
        ) {
            Icon(Icons.Filled.Delete, contentDescription = null, tint = cs.error)
        }
    }
}
