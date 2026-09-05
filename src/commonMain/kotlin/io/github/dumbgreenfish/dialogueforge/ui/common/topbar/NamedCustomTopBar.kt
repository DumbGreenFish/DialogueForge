package io.github.dumbgreenfish.dialogueforge.ui.common.topbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun NamedCustomTopBar(
    onMenuClick: (() -> Unit)? = null,
    title: String,
    leading: @Composable RowScope.() -> Unit = {},
    trailing: @Composable RowScope.() -> Unit = {},
) {
    val cs = MaterialTheme.colorScheme
    CustomTopBar(
        onMenuClick = onMenuClick,
        leading = leading,
        trailing = trailing,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = cs.onSurface,
            )
        }
    )
}