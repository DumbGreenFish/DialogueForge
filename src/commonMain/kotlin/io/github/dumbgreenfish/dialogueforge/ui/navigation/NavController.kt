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

    private val _activeTab = MutableStateFlow(NavTab.Characters)
    val activeTab: StateFlow<NavTab> = _activeTab.asStateFlow()

    fun switchTab(tab: NavTab) { _activeTab.value = tab }
    fun getBar(tab: NavTab): NavBar<*> = bars.getValue(tab)
}
