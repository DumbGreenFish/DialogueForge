package io.github.dumbgreenfish.dialogueforge.ui.common.topbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass

@Composable
fun CustomTopBar(
    onMenuClick: (() -> Unit)? = null,
    leading: @Composable RowScope.() -> Unit = {},
    title: @Composable () -> Unit = {},
    trailing: @Composable RowScope.() -> Unit = {},
) {
    if (windowClass == WindowClass.Compact) {
        CompactTopBar(
            leading = leading,
            title = title,
            trailing = trailing,
        )
    } else {
        WideTopBar(
            onMenuClick = onMenuClick,
            leading = leading,
            title = title,
            trailing = trailing,
        )
    }
}
