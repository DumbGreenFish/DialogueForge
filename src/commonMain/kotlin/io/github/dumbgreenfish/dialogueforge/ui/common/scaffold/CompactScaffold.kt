package io.github.dumbgreenfish.dialogueforge.ui.common.scaffold

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavController
import io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs.NavTabs
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.ForgeBottomNav
import org.koin.compose.koinInject

@Composable
fun CompactScaffold(
    selectedTab: NavTabs,
    content: @Composable (PaddingValues) -> Unit
) {
    val controller = koinInject<NavController>()
    Scaffold(
        bottomBar = {
            ForgeBottomNav(
                selected = selectedTab,
                onSelect = { controller.switchTab(it) },
            )
        },
        content = content
    )
}
