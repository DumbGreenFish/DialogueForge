package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
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

private val TopBarHeight   = 64.dp
private val TopBarPaddingH = 24.dp

@Composable
fun WideTopBar(selectedTab: NavTab) {
    val cs = MaterialTheme.colorScheme
    val item = navItems.first { it.tab == selectedTab }
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(TopBarHeight)
                .padding(horizontal = TopBarPaddingH),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(item.labelRes),
                style = MaterialTheme.typography.titleMedium,
                color = cs.onSurface,
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = cs.onSurfaceVariant,
                )
            }
            // TODO: user persona avatar (32dp copper square, ForgeShape.avatar) — show when PersonaViewModel exposes a selected persona
        }
        HorizontalDivider(color = cs.outline)
    }
}
