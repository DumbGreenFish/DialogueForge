package io.github.dumbgreenfish.dialogueforge.ui.presets

sealed class PresetsIntent {
    data class UpdateEndpoint(val value: String) : PresetsIntent()
    data class UpdateApiKey(val value: String) : PresetsIntent()
    data class UpdateModel(val value: String) : PresetsIntent()
    data class UpdateTemperature(val value: Float) : PresetsIntent()
    data class UpdateMaxTokens(val value: Int) : PresetsIntent()
    data object Load : PresetsIntent()
    data object Save : PresetsIntent()
}
