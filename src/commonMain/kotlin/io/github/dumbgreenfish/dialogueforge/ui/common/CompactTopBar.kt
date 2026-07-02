package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.navItems
import org.jetbrains.compose.resources.stringResource

private val TopBarHeight           = 64.dp
private val TopBarPaddingH         = 4.dp
private val ForgeMarkContainerSize = 48.dp
private val ForgeMarkSize          = 26.dp
private val ForgeMarkTitlePad      = 4.dp

@Composable
fun CompactTopBar(selectedTab: NavTab) {
    val cs = MaterialTheme.colorScheme
    val item = navItems.first { it.tab == selectedTab }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(TopBarHeight)
            .padding(horizontal = TopBarPaddingH),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(ForgeMarkContainerSize),
            contentAlignment = Alignment.Center,
        ) {
            ForgeMark(Modifier.size(ForgeMarkSize))
        }
        Text(
            text = stringResource(item.labelRes),
            style = MaterialTheme.typography.headlineSmall,
            color = cs.onSurface,
            modifier = Modifier.weight(1f).padding(start = ForgeMarkTitlePad),
        )
    }
}
