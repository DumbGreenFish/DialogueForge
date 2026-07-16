package io.github.dumbgreenfish.dialogueforge.ui.settings

import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharactersViewMode
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.AnimationSpeed
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth

sealed class SettingsIntent {
    data class UpdateDensityScale(val value: Float) : SettingsIntent()
    data class UpdateFontScale(val value: Float) : SettingsIntent()
    data class UpdateAnimationSpeed(val value: AnimationSpeed) : SettingsIntent()
    data class UpdateDefaultViewMode(val value: CharactersViewMode) : SettingsIntent()
    data class UpdateMessageWidth(val value: MessageWidth) : SettingsIntent()
    data class UpdateComposerMaxHeight(val valueDp: Int) : SettingsIntent()
    data class UpdateSidebarWidth(val valueDp: Int) : SettingsIntent()
    data class UpdateChatBackgroundOpacity(val value: Float) : SettingsIntent()
    data class UpdateChatHeaderOpacity(val value: Float) : SettingsIntent()
    data class UpdateChatComposerOpacity(val value: Float) : SettingsIntent()
    data class UpdateChatBackgroundDim(val value: Float) : SettingsIntent()
    data class SetChatBackground(val bytes: ByteArray) : SettingsIntent()
    data object RemoveChatBackground : SettingsIntent()
    data object Load : SettingsIntent()
    data object Reset : SettingsIntent()
}
