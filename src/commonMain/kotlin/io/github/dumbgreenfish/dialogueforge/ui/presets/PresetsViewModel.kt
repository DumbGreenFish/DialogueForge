package io.github.dumbgreenfish.dialogueforge.ui.presets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.PresetsSavedNotifier
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class PresetsViewModel(
    private val settings: SettingsRepository,
    private val notifier: PresetsSavedNotifier,
) : ViewModel() {
    private val _state = MutableStateFlow(PresetsState())
    val state: StateFlow<PresetsState> = _state.asStateFlow()

    fun handle(intent: PresetsIntent) {
        when (intent) {
            is PresetsIntent.Load -> load()
            is PresetsIntent.Save -> save()
            is PresetsIntent.UpdateEndpoint -> _state.update { it.copy(endpoint = intent.value, isSaved = false) }
            is PresetsIntent.UpdateApiKey -> _state.update { it.copy(apiKey = intent.value, isSaved = false) }
            is PresetsIntent.UpdateModel -> _state.update { it.copy(model = intent.value, isSaved = false) }
            is PresetsIntent.UpdateTemperature -> _state.update { it.copy(temperature = intent.value, isSaved = false) }
            is PresetsIntent.UpdateMaxTokens -> _state.update { it.copy(maxTokens = intent.value, isSaved = false) }
        }
    }

    private fun load() {
        if (_state.value.isLoaded) return
        viewModelScope.launch {
            _state.update {
                it.copy(
                    endpoint = settings.getEndpoint(),
                    apiKey = settings.getApiKey() ?: "",
                    model = settings.getModel(),
                    temperature = settings.getTemperature(),
                    maxTokens = settings.getMaxTokens(),
                    isLoaded = true,
                )
            }
        }
    }

    private fun save() {
        viewModelScope.launch {
            val s = _state.value
            settings.setEndpoint(s.endpoint)
            settings.setApiKey(s.apiKey)
            settings.setModel(s.model)
            settings.setTemperature(s.temperature)
            settings.setMaxTokens(s.maxTokens)
            _state.update { it.copy(isSaved = true) }
            notifier.notifySaved()
        }
    }
}
