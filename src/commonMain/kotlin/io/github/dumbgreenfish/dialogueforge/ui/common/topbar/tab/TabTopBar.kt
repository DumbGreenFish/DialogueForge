package io.github.dumbgreenfish.dialogueforge.ui.common.topbar.tab

import androidx.compose.runtime.Composable
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.header.CharactersCompactTopBar
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.header.CharactersWideTopBar
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass
import io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs.NavTabs

@Composable
fun TabTopBar(tab: NavTabs, onMenuClick: (() -> Unit)?) {
    if (windowClass == WindowClass.Compact) {
        when(tab) {
            NavTabs.Characters -> CharactersCompactTopBar()
            NavTabs.Persona -> CompactTabTopBar(selectedTab = tab)
            NavTabs.Presets -> CompactTabTopBar(selectedTab = tab)
            NavTabs.Settings -> CompactTabTopBar(selectedTab = tab)
        }
    } else {
        when(tab) {
            NavTabs.Characters -> CharactersWideTopBar(onMenuClick = onMenuClick)
            NavTabs.Persona -> WideTabTopBar(selectedTab = tab, onMenuClick = onMenuClick)
            NavTabs.Presets -> WideTabTopBar(selectedTab = tab, onMenuClick = onMenuClick)
            NavTabs.Settings -> WideTabTopBar(selectedTab = tab, onMenuClick = onMenuClick)
        }
    }
}