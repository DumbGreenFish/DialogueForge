package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersView
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.header.CharactersCompactTopBar
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.header.CharactersWideTopBar
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab
import io.github.dumbgreenfish.dialogueforge.ui.persona.PersonaView
import io.github.dumbgreenfish.dialogueforge.ui.presets.PresetsView
import io.github.dumbgreenfish.dialogueforge.ui.settings.SettingsView

@Composable
fun ScaffoldForTab(tab: NavTab) {
    when (windowClass) {
        WindowClass.Compact -> CompactScaffold(tab)
        WindowClass.Tablet  -> TabletScaffold(tab)
        WindowClass.Wide    -> WideScaffold(tab)
    }
}

@Composable
internal fun TabContent(tab: NavTab, modifier: Modifier) {
    when (tab) {
        NavTab.Characters -> CharactersView(modifier = modifier, isCompact = windowClass == WindowClass.Compact)
        NavTab.Persona    -> PersonaView(modifier)
        NavTab.Presets    -> PresetsView(modifier)
        NavTab.Settings   -> SettingsView(modifier)
    }
}

@Composable
internal fun WideTopBarForTab(tab: NavTab, onMenuClick: (() -> Unit)? = null) {
    if (tab == NavTab.Characters) CharactersWideTopBar(onMenuClick = onMenuClick)
    else WideTopBar(selectedTab = tab, onMenuClick = onMenuClick)
}

@Composable
internal fun CompactTopBarForTab(tab: NavTab) {
    if (tab == NavTab.Characters) CharactersCompactTopBar()
    else CompactTopBar(selectedTab = tab)
}
