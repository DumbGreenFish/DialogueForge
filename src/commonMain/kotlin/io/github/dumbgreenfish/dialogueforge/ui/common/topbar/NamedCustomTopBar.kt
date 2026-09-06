package io.github.dumbgreenfish.dialogueforge.ui.common.topbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NamedCustomTopBar(
    onMenuClick: (() -> Unit)? = null,
    title: String,
    onBack: (() -> Unit)? = null,
    leading: @Composable (RowScope.() -> Unit)? = null,
    trailing: @Composable (RowScope.() -> Unit)? = null,
) {
    val cs = MaterialTheme.colorScheme
    CustomTopBar(
        onMenuClick = onMenuClick,
        leading = { if (leading != null) leading() },
        trailing = { if (trailing != null) trailing() },
        title = {
            if (leading == null && onBack == null && onMenuClick == null) {
                Spacer(Modifier.width(12.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = cs.onSurface,
            )
        },
        onBack = onBack
    )
}