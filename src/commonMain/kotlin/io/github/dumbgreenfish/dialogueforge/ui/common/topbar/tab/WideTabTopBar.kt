package io.github.dumbgreenfish.dialogueforge.ui.common.topbar.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Menu
import io.github.dumbgreenfish.dialogueforge.ui.common.topbar.BaseTopBar
import io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs.NavTabs
import org.jetbrains.compose.resources.stringResource

private val MenuIconGap = 8.dp

@Composable
fun WideTabTopBar(selectedTab: NavTabs, onMenuClick: (() -> Unit)? = null) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxWidth()) {
        BaseTopBar(
            isCompact = false,
            backgroundColor = cs.background,
            leading = {
                if (onMenuClick != null) {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Lucide.Menu,
                            contentDescription = null,
                            tint = cs.onSurfaceVariant,
                        )
                    }
                    Spacer(Modifier.width(MenuIconGap))
                }
            },
            title = {
                Text(
                    text = stringResource(selectedTab.labelRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = cs.onSurface,
                )
            },
        )
    }
}
