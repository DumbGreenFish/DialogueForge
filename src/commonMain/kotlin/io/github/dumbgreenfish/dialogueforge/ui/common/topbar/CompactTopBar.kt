package io.github.dumbgreenfish.dialogueforge.ui.common.topbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide

@Composable
fun CompactTopBar(
    onBack: (() -> Unit)? = null,
    leading: @Composable RowScope.() -> Unit = {},
    title: @Composable RowScope.() -> Unit = {},
    trailing: @Composable RowScope.() -> Unit = {},
) {
    val cs = MaterialTheme.colorScheme
    BaseTopBar(
        isCompact = true,
        backgroundColor = cs.background,
        leading = {
            if (onBack != null) {
                DrawGoBackButton(onBack, cs)
            }
            leading()
        },
        title = title,
        trailing = trailing
    )
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