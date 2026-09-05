package io.github.dumbgreenfish.dialogueforge.ui.common.topbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun CompactTopBar(
    leading: @Composable RowScope.() -> Unit = {},
    title: @Composable () -> Unit = {},
    trailing: @Composable RowScope.() -> Unit = {},
) {
    val cs = MaterialTheme.colorScheme
    BaseTopBar(
        isCompact = true,
        backgroundColor = cs.background,
        leading = leading,
        title = title,
        trailing = trailing
    )
}