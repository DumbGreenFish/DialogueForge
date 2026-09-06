package io.github.dumbgreenfish.dialogueforge.ui.common.topbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Menu

private val MenuIconGap = 8.dp

@Composable
fun WideTopBar(
    onMenuClick: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    leading: @Composable RowScope.() -> Unit = {},
    title: @Composable RowScope.() -> Unit = {},
    trailing: @Composable RowScope.() -> Unit = {},
) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxWidth()) {
        BaseTopBar(
            isCompact = false,
            backgroundColor = cs.background,
            leading = {
                if (onMenuClick != null) {
                    DrawSidebarButton(onMenuClick, cs)
                }
                if (onBack != null) {
                    DrawGoBackButton(onBack, cs)
                }
                Spacer(Modifier.width(MenuIconGap))
                if (leading != null) leading()
            },
            title = title,
            trailing = trailing
        )
    }
}

@Composable
private fun DrawGoBackButton(onBack: () -> Unit, cs: ColorScheme) {
    IconButton(onClick = onBack) {
        Icon(
            imageVector = Lucide.ArrowLeft,
            contentDescription = null,
            tint = cs.onSurfaceVariant,
        )
    }
}

@Composable
private fun DrawSidebarButton(onMenuClick: () -> Unit, cs: ColorScheme) {
    IconButton(onClick = onMenuClick) {
        Icon(
            imageVector = Lucide.Menu,
            contentDescription = null,
            tint = cs.onSurfaceVariant,
        )
    }
}