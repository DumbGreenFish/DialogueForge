package io.github.dumbgreenfish.dialogueforge.ui.characters.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import org.jetbrains.compose.resources.stringResource

private val MenuBorderWidth     = 1.dp
private val MenuShadowElevation = 3.dp

@Composable
internal fun CharacterContextMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    actions: List<ContextAction>,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    DropdownMenu(
        expanded         = expanded,
        onDismissRequest = onDismiss,
        modifier         = modifier,
        shape            = MaterialTheme.shapes.small,
        containerColor   = ForgeColors.surfaceContainerHigh,
        tonalElevation   = 0.dp,
        shadowElevation  = MenuShadowElevation,
        border           = BorderStroke(MenuBorderWidth, cs.outline),
    ) {
        actions.forEach { action ->
            DropdownMenuItem(
                text    = {
                    Text(
                        text  = stringResource(action.labelRes),
                        color = if (action.destructive) cs.error else Color.Unspecified,
                    )
                },
                onClick = {
                    action.onClick()
                    onDismiss()
                },
                enabled     = action.enabled,
                leadingIcon = {
                    Icon(
                        imageVector        = action.icon,
                        contentDescription = null,
                        tint               = if (action.destructive) cs.error else LocalContentColor.current,
                    )
                },
            )
        }
    }
}