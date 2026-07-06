package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersView
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.header.CharactersWideTopBar
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavController
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavigationSidebar
import io.github.dumbgreenfish.dialogueforge.ui.persona.PersonaView
import io.github.dumbgreenfish.dialogueforge.ui.presets.PresetsView
import io.github.dumbgreenfish.dialogueforge.ui.settings.SettingsView
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun TabletScaffold(selectedTab: NavTab) {
    val controller = koinInject<NavController>()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            NavigationSidebar(
                selected = selectedTab,
                onSelect = { tab ->
                    controller.switchTab(tab)
                    scope.launch { drawerState.close() }
                },
            )
        },
    ) {
        Scaffold { innerPadding ->
            Column(Modifier.fillMaxSize().padding(innerPadding).clipToBounds()) {
                if (selectedTab == NavTab.Characters) CharactersWideTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
                else WideTopBar(
                    selectedTab = selectedTab,
                    onMenuClick = { scope.launch { drawerState.open() } },
                )
                val contentModifier = Modifier.fillMaxSize().weight(1f)
                when (selectedTab) {
                    NavTab.Characters -> CharactersView(modifier = contentModifier, isCompact = false)
                    NavTab.Persona    -> PersonaView(contentModifier)
                    NavTab.Presets    -> PresetsView(contentModifier)
                    NavTab.Settings   -> SettingsView(contentModifier)
                }
            }
        }
    }
}
