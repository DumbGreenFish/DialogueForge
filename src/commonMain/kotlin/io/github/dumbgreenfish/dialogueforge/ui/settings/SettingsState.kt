package io.github.dumbgreenfish.dialogueforge.ui.settings

import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharactersViewMode
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.AnimationSpeed
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth

data class SettingsState(
    val densityScale: Float = 1.0f,
    val fontScale: Float = 1.0f,
    val animationSpeed: AnimationSpeed = AnimationSpeed.Normal,
    val defaultViewMode: CharactersViewMode = CharactersViewMode.List,
    val messageWidth: MessageWidth = MessageWidth.Normal,
    val composerMaxHeightDp: Int = 160,
    val sidebarWidthDp: Int = 240,
    val isLoaded: Boolean = false,
)
