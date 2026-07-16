package io.github.dumbgreenfish.dialogueforge.data.repository.settings

import io.github.dumbgreenfish.dialogueforge.design.ForgeAnimation
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharactersViewMode
import io.github.dumbgreenfish.dialogueforge.ui.settings.SettingsState
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.AnimationSpeed
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

/**
 * Single source of truth for user settings: holds the canonical [SettingsState],
 * exposes per-field projections for app-wide readers, and persists each change
 * (one key at a time) through [SettingsRepository].
 */
@Single
class ForgeSettings(
    private val settings: SettingsRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    val densityScale: StateFlow<Float> = project { it.densityScale }
    val fontScale: StateFlow<Float> = project { it.fontScale }
    val animationSpeed: StateFlow<AnimationSpeed> = project { it.animationSpeed }
    val defaultViewMode: StateFlow<CharactersViewMode> = project { it.defaultViewMode }
    val messageWidth: StateFlow<MessageWidth> = project { it.messageWidth }
    val composerMaxHeightDp: StateFlow<Int> = project { it.composerMaxHeightDp }
    val sidebarWidthDp: StateFlow<Int> = project { it.sidebarWidthDp }
    val chatBackgroundBytes: StateFlow<ByteArray?> = project { it.chatBackgroundBytes }
    val chatBackgroundOpacity: StateFlow<Float> = project { it.chatBackgroundOpacity }
    val chatHeaderOpacity: StateFlow<Float> = project { it.chatHeaderOpacity }
    val chatComposerOpacity: StateFlow<Float> = project { it.chatComposerOpacity }
    val chatBackgroundDim: StateFlow<Float> = project { it.chatBackgroundDim }
    val hasCompletedFirstLaunch: StateFlow<Boolean> = project { it.hasCompletedFirstLaunch }

    init {
        scope.launch { loadAll() }
    }

    fun setDensityScale(value: Float) = update({ it.copy(densityScale = value) }) { settings.setDensityScale(value) }

    fun setFontScale(value: Float) = update({ it.copy(fontScale = value) }) { settings.setFontScale(value) }

    fun setAnimationSpeed(value: AnimationSpeed) {
        ForgeAnimation.setSpeedMultiplier(value.durationMultiplier)
        update({ it.copy(animationSpeed = value) }) { settings.setAnimationSpeed(value.name) }
    }

    fun setDefaultViewMode(value: CharactersViewMode) =
        update({ it.copy(defaultViewMode = value) }) { settings.setDefaultViewMode(value.name) }

    fun setMessageWidth(value: MessageWidth) =
        update({ it.copy(messageWidth = value) }) { settings.setMessageWidth(value.name) }

    fun setComposerMaxHeight(value: Int) =
        update({ it.copy(composerMaxHeightDp = value) }) { settings.setComposerMaxHeight(value) }

    fun setSidebarWidth(value: Int) =
        update({ it.copy(sidebarWidthDp = value) }) { settings.setSidebarWidth(value) }

    fun setChatBackground(bytes: ByteArray?, opacity: Float) =
        update({ it.copy(chatBackgroundBytes = bytes, chatBackgroundOpacity = opacity) }) {
            settings.setChatBackgroundBytes(bytes)
            settings.setChatBackgroundOpacity(opacity)
        }

    fun setChatBackgroundOpacity(value: Float) =
        update({ it.copy(chatBackgroundOpacity = value) }) { settings.setChatBackgroundOpacity(value) }

    fun setChatHeaderOpacity(value: Float) =
        update({ it.copy(chatHeaderOpacity = value) }) { settings.setChatHeaderOpacity(value) }

    fun setChatComposerOpacity(value: Float) =
        update({ it.copy(chatComposerOpacity = value) }) { settings.setChatComposerOpacity(value) }

    fun setChatBackgroundDim(value: Float) =
        update({ it.copy(chatBackgroundDim = value) }) { settings.setChatBackgroundDim(value) }

    fun removeChatBackground() =
        update({ it.copy(chatBackgroundBytes = null) }) { settings.setChatBackgroundBytes(null) }

    fun setHasCompletedFirstLaunch() =
        update({ it.copy(hasCompletedFirstLaunch = true) }) { settings.setHasCompletedFirstLaunch(true) }

    fun reset() {
        val defaults = SettingsState(isLoaded = true)
        _state.value = defaults
        ForgeAnimation.setSpeedMultiplier(defaults.animationSpeed.durationMultiplier)
        scope.launch {
            settings.setDensityScale(defaults.densityScale)
            settings.setFontScale(defaults.fontScale)
            settings.setAnimationSpeed(defaults.animationSpeed.name)
            settings.setDefaultViewMode(defaults.defaultViewMode.name)
            settings.setMessageWidth(defaults.messageWidth.name)
            settings.setComposerMaxHeight(defaults.composerMaxHeightDp)
            settings.setSidebarWidth(defaults.sidebarWidthDp)
            settings.setChatHeaderOpacity(defaults.chatHeaderOpacity)
            settings.setChatComposerOpacity(defaults.chatComposerOpacity)
            settings.setChatBackgroundDim(defaults.chatBackgroundDim)
        }
    }

    private suspend fun loadAll() {
        val loaded = SettingsState(
            densityScale = settings.getDensityScale(),
            fontScale = settings.getFontScale(),
            animationSpeed = safeEnum(settings.getAnimationSpeed(), AnimationSpeed.Normal),
            defaultViewMode = safeEnum(settings.getDefaultViewMode(), CharactersViewMode.List),
            messageWidth = safeEnum(settings.getMessageWidth(), MessageWidth.Normal),
            composerMaxHeightDp = settings.getComposerMaxHeight(),
            sidebarWidthDp = settings.getSidebarWidth(),
            chatBackgroundBytes = settings.getChatBackgroundBytes(),
            chatBackgroundOpacity = settings.getChatBackgroundOpacity(),
            chatHeaderOpacity = settings.getChatHeaderOpacity(),
            chatComposerOpacity = settings.getChatComposerOpacity(),
            chatBackgroundDim = settings.getChatBackgroundDim(),
            hasCompletedFirstLaunch = settings.getHasCompletedFirstLaunch(),
            isLoaded = true,
        )
        _state.value = loaded
        ForgeAnimation.setSpeedMultiplier(loaded.animationSpeed.durationMultiplier)
    }

    private fun update(transform: (SettingsState) -> SettingsState, persist: suspend () -> Unit) {
        _state.update(transform)
        scope.launch { persist() }
    }

    private fun <T> project(selector: (SettingsState) -> T): StateFlow<T> =
        _state.map(selector).stateIn(scope, SharingStarted.Eagerly, selector(_state.value))

    private inline fun <reified T : Enum<T>> safeEnum(name: String, default: T): T =
        try { enumValueOf<T>(name) } catch (_: IllegalArgumentException) { default }
}
