package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.navItems
import org.jetbrains.compose.resources.stringResource

internal val WideTopBarHeight   = 64.dp
internal val WideTopBarPaddingH = 24.dp

@Composable
fun WideTopBar(selectedTab: NavTab, onMenuClick: (() -> Unit)? = null) {
    val cs = MaterialTheme.colorScheme
    val item = navItems.first { it.tab == selectedTab }
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(WideTopBarHeight)
                .padding(horizontal = WideTopBarPaddingH),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onMenuClick != null) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = null,
                        tint = cs.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = stringResource(item.labelRes),
                style = MaterialTheme.typography.titleMedium,
                color = cs.onSurface,
            )
        }
        HorizontalDivider(color = cs.outline)
    }
}
