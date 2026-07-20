package io.github.dumbgreenfish.dialogueforge.ui.settings

import io.github.dumbgreenfish.dialogueforge.data.repository.settings.SettingsRepository
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharactersViewMode
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.AnimationSpeed
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth

data class SettingsState(
    val densityScale: Float = SettingsRepository.DEFAULT_DENSITY_SCALE,
    val fontScale: Float = SettingsRepository.DEFAULT_FONT_SCALE,
    val animationSpeed: AnimationSpeed = AnimationSpeed.Normal,
    val defaultViewMode: CharactersViewMode = CharactersViewMode.List,
    val messageWidth: MessageWidth = MessageWidth.Normal,
    val composerMaxHeightDp: Int = SettingsRepository.DEFAULT_COMPOSER_MAX_HEIGHT,
    val sidebarWidthDp: Int = SettingsRepository.DEFAULT_SIDEBAR_WIDTH,
    val chatBackgroundBytes: ByteArray? = null,
    val chatBackgroundOpacity: Float = SettingsRepository.DEFAULT_CHAT_BACKGROUND_OPACITY,
    val chatHeaderOpacity: Float = SettingsRepository.DEFAULT_CHAT_HEADER_OPACITY,
    val chatComposerOpacity: Float = SettingsRepository.DEFAULT_CHAT_COMPOSER_OPACITY,
    val chatBackgroundDim: Float = SettingsRepository.DEFAULT_CHAT_BACKGROUND_DIM,
    val hasCompletedFirstLaunch: Boolean = false,
    val airiVersion: Int = 0,
    val airiUpdateAvailable: Boolean = false,
    val isLoaded: Boolean = false,
)
