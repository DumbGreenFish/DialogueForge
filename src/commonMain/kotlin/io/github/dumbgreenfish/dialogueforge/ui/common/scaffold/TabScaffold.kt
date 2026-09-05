package io.github.dumbgreenfish.dialogueforge.ui.common.scaffold

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersView
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.topbar.tab.TabTopBar
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavController
import io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs.NavTabs
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavigationSidebar
import io.github.dumbgreenfish.dialogueforge.ui.persona.PersonaView
import io.github.dumbgreenfish.dialogueforge.ui.presets.PresetsView
import io.github.dumbgreenfish.dialogueforge.ui.settings.SettingsView
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun TabScaffoldWithMainScreen(tab: NavTabs) {
    TabScaffold(tab, { modifier ->
        TabContent(tab, modifier)
    })
}

@Composable
fun TabScaffold(tab: NavTabs, content: @Composable (Modifier) -> Unit) {
    TabScaffoldWithCustomTopBar(
        tab = tab,
        topBar = { onMenuClick -> TabTopBar(tab, onMenuClick) },
        content = content,
    )
}

@Composable
fun TabScaffoldWithCustomTopBar(
    tab: NavTabs,
    topBar: @Composable (onMenuClick: (() -> Unit)?) -> Unit,
    content: @Composable (Modifier) -> Unit,
) {
    val controller = koinInject<NavController>()
    when (windowClass) {
        WindowClass.Compact -> CompactScaffold(tab) { innerPadding ->
            Column(Modifier.fillMaxSize().padding(innerPadding).clipToBounds()) {
                topBar(null)
                content(Modifier.fillMaxSize().weight(1f))
            }
        }

        WindowClass.Tablet -> TabletScaffold(tab) { innerPadding, scope, drawerState ->
            Column(Modifier.fillMaxSize().padding(innerPadding).clipToBounds()) {
                topBar { scope.launch { drawerState.open() } }
                content(Modifier.fillMaxSize().weight(1f))
            }
        }

        WindowClass.Wide -> WideScaffold { innerPadding ->
            Row(Modifier.fillMaxSize().padding(innerPadding)) {
                NavigationSidebar(
                    selected = tab,
                    onSelect = { controller.switchTab(it) },
                )
                VerticalDivider()
                Column(Modifier.weight(1f).fillMaxHeight().clipToBounds()) {
                    topBar(null)
                    content(Modifier.fillMaxSize().weight(1f))
                }
            }
        }
    }
}

@Composable
private fun TabContent(tab: NavTabs, modifier: Modifier) {
    when (tab) {
        NavTabs.Characters -> CharactersView(modifier)
        NavTabs.Persona -> PersonaView(modifier)
        NavTabs.Presets -> PresetsView(modifier)
        NavTabs.Settings -> SettingsView(modifier)
    }
}
