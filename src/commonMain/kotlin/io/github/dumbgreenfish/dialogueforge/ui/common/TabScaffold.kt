package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersView
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.header.CharactersCompactTopBar
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.header.CharactersWideTopBar
import io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs.NavTabs
import io.github.dumbgreenfish.dialogueforge.ui.persona.PersonaView
import io.github.dumbgreenfish.dialogueforge.ui.presets.PresetsView
import io.github.dumbgreenfish.dialogueforge.ui.settings.SettingsView

@Composable
fun ScaffoldForTab(tab: NavTabs) {
    when (windowClass) {
        WindowClass.Compact -> CompactScaffold(tab)
        WindowClass.Tablet  -> TabletScaffold(tab)
        WindowClass.Wide    -> WideScaffold(tab)
    }
}

@Composable
internal fun TabContent(tab: NavTabs, modifier: Modifier) {
    when (tab) {
        NavTabs.Characters -> CharactersView(modifier = modifier, isCompact = windowClass == WindowClass.Compact)
        NavTabs.Persona    -> PersonaView(modifier)
        NavTabs.Presets    -> PresetsView(modifier)
        NavTabs.Settings   -> SettingsView(modifier)
    }
}

@Composable
internal fun WideTopBarForTab(tab: NavTabs, onMenuClick: (() -> Unit)? = null) {
    if (tab == NavTabs.Characters) CharactersWideTopBar(onMenuClick = onMenuClick)
    else WideTopBar(selectedTab = tab, onMenuClick = onMenuClick)
}

@Composable
internal fun CompactTopBarForTab(tab: NavTabs) {
    if (tab == NavTabs.Characters) CharactersCompactTopBar()
    else CompactTopBar(selectedTab = tab)
}
