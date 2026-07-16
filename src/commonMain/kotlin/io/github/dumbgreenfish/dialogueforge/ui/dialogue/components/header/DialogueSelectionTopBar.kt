package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.header

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Trash2
import com.composables.icons.lucide.X
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_selection_count
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.components.BaseTopBar
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass
import org.jetbrains.compose.resources.pluralStringResource

@Composable
internal fun DialogueSelectionTopBar(
    selectedCount: Int,
    backgroundOpacity: Float,
    onClearSelection: () -> Unit,
    onCopySelected: () -> Unit,
    onDeleteSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    BaseTopBar(
        isCompact = windowClass == WindowClass.Compact,
        backgroundColor = cs.background.copy(alpha = backgroundOpacity),
        modifier = modifier,
        leading = {
            IconButton(onClick = onClearSelection) {
                Icon(
                    imageVector = Lucide.X,
                    contentDescription = null,
                    tint = cs.onSurfaceVariant,
                )
            }
        },
        title = {
            Text(
                text = pluralStringResource(
                    Res.plurals.dialogue_selection_count,
                    selectedCount,
                    selectedCount,
                ),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = cs.onSurface,
            )
        },
        trailing = {
            IconButton(onClick = onCopySelected) {
                Icon(
                    imageVector = Lucide.Copy,
                    contentDescription = null,
                    tint = cs.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(12.dp))
            IconButton(onClick = onDeleteSelected) {
                Icon(
                    imageVector = Lucide.Trash2,
                    contentDescription = null,
                    tint = cs.error,
                )
            }
        },
    )
}
