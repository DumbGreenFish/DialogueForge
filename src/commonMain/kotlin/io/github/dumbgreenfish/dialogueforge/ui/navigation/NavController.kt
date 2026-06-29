package io.github.dumbgreenfish.dialogueforge.ui.navigation

import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

@Single
class NavController {
    private val bars = mapOf(
        NavTab.Characters to CharactersTab(),
        NavTab.Persona to PersonaTab(),
        NavTab.Presets to PresetsTab(),
        NavTab.Settings to SettingsTab(),
    )

    private val _currentBar = MutableStateFlow(bars.get(NavTab.Characters)!!)
    val currentBar: StateFlow<NavBar<*>> = _currentBar.asStateFlow()

    fun switchTab(tab: NavTab) { _currentBar.value = bars[tab]!! }
}
