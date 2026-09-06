package io.github.dumbgreenfish.dialogueforge.ui.common.topbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass

@Composable
fun CustomTopBar(
    onMenuClick: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    leading: @Composable RowScope.() -> Unit = {},
    title: @Composable RowScope.() -> Unit = {},
    trailing: @Composable RowScope.() -> Unit = {},
) {
    if (windowClass == WindowClass.Compact) {
        CompactTopBar(
            onBack = onBack,
            leading = leading,
            title = title,
            trailing = trailing,
        )
    } else {
        WideTopBar(
            onMenuClick = onMenuClick,
            onBack = onBack,
            leading = leading,
            title = title,
            trailing = trailing,
        )
    }
}
