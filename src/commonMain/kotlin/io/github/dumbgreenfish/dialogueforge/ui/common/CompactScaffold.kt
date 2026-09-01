package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavController
import io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs.NavTabs
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.ForgeBottomNav
import org.koin.compose.koinInject

@Composable
fun CompactScaffold(selectedTab: NavTabs) {
    val controller = koinInject<NavController>()
    Scaffold(
        topBar = { CompactTopBarForTab(selectedTab) },
        bottomBar = {
            ForgeBottomNav(
                selected = selectedTab,
                onSelect = { controller.switchTab(it) },
            )
        },
    ) { innerPadding ->
        TabContent(selectedTab, Modifier.fillMaxSize().padding(innerPadding))
    }
}
