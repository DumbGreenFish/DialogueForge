package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersView
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavController
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.ForgeBottomNav
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab
import io.github.dumbgreenfish.dialogueforge.ui.persona.PersonaView
import io.github.dumbgreenfish.dialogueforge.ui.presets.PresetsView
import io.github.dumbgreenfish.dialogueforge.ui.settings.SettingsView
import org.koin.compose.koinInject

@Composable
fun CompactScaffold(selectedTab: NavTab) {
    val controller = koinInject<NavController>()
    Scaffold(
        topBar = { CompactTopBar(selectedTab) },
        bottomBar = {
            ForgeBottomNav(
                selected = selectedTab,
                onSelect = { controller.switchTab(it) },
            )
        },
    ) { innerPadding ->
        val contentModifier = Modifier.fillMaxSize().padding(innerPadding)
        when (selectedTab) {
            NavTab.Characters -> CharactersView(modifier = contentModifier, isCompact = true)
            NavTab.Persona    -> PersonaView(contentModifier)
            NavTab.Presets    -> PresetsView(contentModifier)
            NavTab.Settings   -> SettingsView(contentModifier)
        }
    }
}