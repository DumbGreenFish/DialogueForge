package io.github.dumbgreenfish.dialogueforge.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.ForgeSettings
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.SettingsRepository
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharactersViewMode
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.AnimationSpeed
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel(
    private val settings: SettingsRepository,
    private val forgeSettings: ForgeSettings,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun handle(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.Load -> load()
            is SettingsIntent.Reset -> reset()
            is SettingsIntent.UpdateDensityScale -> updateAndSave { it.copy(densityScale = intent.value) }
            is SettingsIntent.UpdateFontScale -> updateAndSave { it.copy(fontScale = intent.value) }
            is SettingsIntent.UpdateAnimationSpeed -> updateAndSave { it.copy(animationSpeed = intent.value) }
            is SettingsIntent.UpdateDefaultViewMode -> updateAndSave { it.copy(defaultViewMode = intent.value) }
            is SettingsIntent.UpdateMessageWidth -> updateAndSave { it.copy(messageWidth = intent.value) }
            is SettingsIntent.UpdateComposerMaxHeight -> updateAndSave { it.copy(composerMaxHeightDp = intent.valueDp) }
            is SettingsIntent.UpdateSidebarWidth -> updateAndSave { it.copy(sidebarWidthDp = intent.valueDp) }
        }
    }

    private fun load() {
        if (_state.value.isLoaded) return
        viewModelScope.launch {
            val state = SettingsState(
                densityScale = settings.getDensityScale(),
                fontScale = settings.getFontScale(),
                animationSpeed = try {
                    AnimationSpeed.valueOf(settings.getAnimationSpeed())
                } catch (_: IllegalArgumentException) {
                    AnimationSpeed.Normal
                },
                defaultViewMode = try {
                    CharactersViewMode.valueOf(settings.getDefaultViewMode())
                } catch (_: IllegalArgumentException) {
                    CharactersViewMode.List
                },
                messageWidth = try {
                    MessageWidth.valueOf(settings.getMessageWidth())
                } catch (_: IllegalArgumentException) {
                    MessageWidth.Normal
                },
                composerMaxHeightDp = settings.getComposerMaxHeight(),
                sidebarWidthDp = settings.getSidebarWidth(),
                isLoaded = true,
            )
            _state.update { state }
            forgeSettings.applyState(state)
        }
    }

    private fun reset() {
        val defaults = SettingsState()
        _state.value = defaults
        forgeSettings.applyState(defaults)
        viewModelScope.launch {
            settings.setDensityScale(SettingsRepository.DEFAULT_DENSITY_SCALE)
            settings.setFontScale(SettingsRepository.DEFAULT_FONT_SCALE)
            settings.setAnimationSpeed(SettingsRepository.DEFAULT_ANIMATION_SPEED)
            settings.setDefaultViewMode(SettingsRepository.DEFAULT_VIEW_MODE)
            settings.setMessageWidth(SettingsRepository.DEFAULT_MESSAGE_WIDTH)
            settings.setComposerMaxHeight(SettingsRepository.DEFAULT_COMPOSER_MAX_HEIGHT)
            settings.setSidebarWidth(SettingsRepository.DEFAULT_SIDEBAR_WIDTH)
        }
    }

    private fun updateAndSave(transform: (SettingsState) -> SettingsState) {
        _state.update(transform)
        val s = _state.value
        forgeSettings.applyState(s)
        viewModelScope.launch {
            settings.setDensityScale(s.densityScale)
            settings.setFontScale(s.fontScale)
            settings.setAnimationSpeed(s.animationSpeed.name)
            settings.setDefaultViewMode(s.defaultViewMode.name)
            settings.setMessageWidth(s.messageWidth.name)
            settings.setComposerMaxHeight(s.composerMaxHeightDp)
            settings.setSidebarWidth(s.sidebarWidthDp)
        }
    }
}
