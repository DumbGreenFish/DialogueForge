package io.github.dumbgreenfish.dialogueforge.ui.settings

import androidx.lifecycle.ViewModel
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.ForgeSettings
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel(
    private val forgeSettings: ForgeSettings,
) : ViewModel() {
    val state: StateFlow<SettingsState> = forgeSettings.state

    fun handle(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.Load -> Unit
            is SettingsIntent.Reset -> forgeSettings.reset()
            is SettingsIntent.UpdateDensityScale -> forgeSettings.setDensityScale(intent.value)
            is SettingsIntent.UpdateFontScale -> forgeSettings.setFontScale(intent.value)
            is SettingsIntent.UpdateAnimationSpeed -> forgeSettings.setAnimationSpeed(intent.value)
            is SettingsIntent.UpdateDefaultViewMode -> forgeSettings.setDefaultViewMode(intent.value)
            is SettingsIntent.UpdateMessageWidth -> forgeSettings.setMessageWidth(intent.value)
            is SettingsIntent.UpdateComposerMaxHeight -> forgeSettings.setComposerMaxHeight(intent.valueDp)
            is SettingsIntent.UpdateSidebarWidth -> forgeSettings.setSidebarWidth(intent.valueDp)
        }
    }
}
