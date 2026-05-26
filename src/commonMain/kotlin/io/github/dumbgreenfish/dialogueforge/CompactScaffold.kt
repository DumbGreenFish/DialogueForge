package io.github.dumbgreenfish.dialogueforge

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersView
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.ForgeBottomNav
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab
import io.github.dumbgreenfish.dialogueforge.ui.persona.PersonaView
import io.github.dumbgreenfish.dialogueforge.ui.presets.PresetsView
import io.github.dumbgreenfish.dialogueforge.ui.settings.SettingsView

@Composable
fun CompactScaffold(selectedTab: NavTab, onTabChange: (NavTab) -> Unit) {
    Scaffold(
        bottomBar = {
            ForgeBottomNav(
                selected = selectedTab,
                onSelect = onTabChange,
            )
        },
    ) { innerPadding ->
        val contentModifier = Modifier.fillMaxSize().padding(innerPadding)
        when (selectedTab) {
            NavTab.Characters -> CharactersView(contentModifier)
            NavTab.Persona    -> PersonaView(contentModifier)
            NavTab.Presets    -> PresetsView(contentModifier)
            NavTab.Settings   -> SettingsView(contentModifier)
        }
    }
}