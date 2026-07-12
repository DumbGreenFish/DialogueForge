package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavController
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavigationSidebar
import org.koin.compose.koinInject

@Composable
fun WideScaffold(selectedTab: NavTab) {
    val controller = koinInject<NavController>()
    Scaffold { innerPadding ->
        Row(Modifier.fillMaxSize().padding(innerPadding)) {
            NavigationSidebar(
                selected = selectedTab,
                onSelect = { controller.switchTab(it) },
            )
            VerticalDivider()
            Column(Modifier.weight(1f).fillMaxHeight().clipToBounds()) {
                WideTopBarForTab(selectedTab)
                TabContent(selectedTab, Modifier.fillMaxSize().weight(1f))
            }
        }
    }
}
