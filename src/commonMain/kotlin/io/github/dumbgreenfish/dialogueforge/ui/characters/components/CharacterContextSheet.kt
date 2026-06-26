package io.github.dumbgreenfish.dialogueforge.ui.characters.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import org.jetbrains.compose.resources.stringResource

private val TitlePaddingH      = 24.dp
private val TitlePaddingBottom = 8.dp
private val SheetBottomPadding = 8.dp
private val RowHeight          = 56.dp
private val RowPaddingH        = 24.dp
private val RowGap             = 16.dp
private val RowIconSize        = 24.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CharacterContextSheet(
    expanded: Boolean,
    onDismiss: () -> Unit,
    actions: List<ContextAction>,
) {
    if (!expanded) return
    val cs = MaterialTheme.colorScheme
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = cs.surfaceVariant,
    ) {
        Column(Modifier.fillMaxWidth().padding(bottom = SheetBottomPadding)) {
            actions.forEach { action ->
                ContextSheetRow(action = action, onHandled = onDismiss)
            }
        }
    }
}

@Composable
private fun ContextSheetRow(action: ContextAction, onHandled: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val contentColor = when {
        !action.enabled    -> ForgeColors.onSurfaceMute
        action.destructive -> cs.error
        else               -> cs.onSurface
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(RowHeight)
            .then(
                if (action.enabled) {
                    Modifier.clickable {
                        action.onClick()
                        onHandled()
                    }
                } else {
                    Modifier
                }
            )
            .padding(horizontal = RowPaddingH),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(RowGap),
    ) {
        Icon(action.icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(RowIconSize))
        Text(stringResource(action.labelRes), style = MaterialTheme.typography.bodyLarge, color = contentColor)
    }
}