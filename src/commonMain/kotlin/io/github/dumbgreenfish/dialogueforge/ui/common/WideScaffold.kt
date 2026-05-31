package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersView
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavigationSidebar
import io.github.dumbgreenfish.dialogueforge.ui.persona.PersonaView
import io.github.dumbgreenfish.dialogueforge.ui.presets.PresetsView
import io.github.dumbgreenfish.dialogueforge.ui.settings.SettingsView

@Composable
fun WideScaffold(selectedTab: NavTab, onTabChange: (NavTab) -> Unit) {
    Scaffold { innerPadding ->
        Row(Modifier.fillMaxSize().padding(innerPadding)) {
            NavigationSidebar(
                selected = selectedTab,
                onSelect = onTabChange,
            )
            VerticalDivider()
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