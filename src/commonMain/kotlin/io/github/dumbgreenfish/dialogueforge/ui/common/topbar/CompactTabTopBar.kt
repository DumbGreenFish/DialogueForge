package io.github.dumbgreenfish.dialogueforge.ui.common.topbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.ui.common.ForgeMark
import io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs.NavTabs
import org.jetbrains.compose.resources.stringResource

private val ForgeMarkContainerSize = 48.dp
private val ForgeMarkSize          = 26.dp
private val ForgeMarkTitlePad      = 4.dp

@Composable
fun CompactTabTopBar(selectedTab: NavTabs) {
    val cs = MaterialTheme.colorScheme
    BaseTopBar(
        isCompact = true,
        backgroundColor = cs.background,
        leading = {
            Box(
                modifier = Modifier.size(ForgeMarkContainerSize),
                contentAlignment = Alignment.Center,
            ) {
                ForgeMark(
                    Modifier.size(ForgeMarkSize)
                )
            }
        },
        title = {
            Text(
                text = stringResource(selectedTab.labelRes),
                style = MaterialTheme.typography.headlineSmall,
                color = cs.onSurface,
                modifier = Modifier.padding(start = ForgeMarkTitlePad),
            )
        },
    )
}
