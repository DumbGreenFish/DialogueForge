package io.github.dumbgreenfish.dialogueforge.ui.presets

import io.github.dumbgreenfish.dialogueforge.data.repository.settings.SettingsRepository

data class PresetsState(
    val endpoint: String = SettingsRepository.DEFAULT_ENDPOINT,
    val apiKey: String = "",
    val model: String = SettingsRepository.DEFAULT_MODEL,
    val temperature: Float = SettingsRepository.DEFAULT_TEMPERATURE,
    val maxTokens: Int = SettingsRepository.DEFAULT_MAX_TOKENS,
    val isSaved: Boolean = false,
    val isLoaded: Boolean = false,
)
