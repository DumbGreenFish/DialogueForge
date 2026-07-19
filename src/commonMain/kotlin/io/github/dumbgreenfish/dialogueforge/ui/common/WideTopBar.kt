package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.ui.common.components.BaseTopBar
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.navItems
import org.jetbrains.compose.resources.stringResource

private val MenuIconGap = 8.dp

@Composable
fun WideTopBar(selectedTab: NavTab, onMenuClick: (() -> Unit)? = null) {
    val cs = MaterialTheme.colorScheme
    val item = navItems.first { it.tab == selectedTab }
    Column(Modifier.fillMaxWidth()) {
        BaseTopBar(
            isCompact = false,
            backgroundColor = cs.background,
            leading = {
                if (onMenuClick != null) {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = null,
                            tint = cs.onSurfaceVariant,
                        )
                    }
                    Spacer(Modifier.width(MenuIconGap))
                }
            },
            title = {
                Text(
                    text = stringResource(item.labelRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = cs.onSurface,
                )
            },
        )
        HorizontalDivider(color = cs.outline)
    }
}
