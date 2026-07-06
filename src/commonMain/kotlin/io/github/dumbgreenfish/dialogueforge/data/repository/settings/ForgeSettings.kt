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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@Single
class ForgeSettings(
    private val settings: SettingsRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _densityScale = MutableStateFlow(SettingsRepository.DEFAULT_DENSITY_SCALE)
    val densityScale: StateFlow<Float> = _densityScale.asStateFlow()

    private val _fontScale = MutableStateFlow(SettingsRepository.DEFAULT_FONT_SCALE)
    val fontScale: StateFlow<Float> = _fontScale.asStateFlow()

    private val _animationSpeed = MutableStateFlow(AnimationSpeed.Normal)
    val animationSpeed: StateFlow<AnimationSpeed> = _animationSpeed.asStateFlow()

    private val _defaultViewMode = MutableStateFlow(CharactersViewMode.List)
    val defaultViewMode: StateFlow<CharactersViewMode> = _defaultViewMode.asStateFlow()

    private val _messageWidth = MutableStateFlow(MessageWidth.Normal)
    val messageWidth: StateFlow<MessageWidth> = _messageWidth.asStateFlow()

    private val _composerMaxHeightDp = MutableStateFlow(SettingsRepository.DEFAULT_COMPOSER_MAX_HEIGHT)
    val composerMaxHeightDp: StateFlow<Int> = _composerMaxHeightDp.asStateFlow()

    private val _sidebarWidthDp = MutableStateFlow(SettingsRepository.DEFAULT_SIDEBAR_WIDTH)
    val sidebarWidthDp: StateFlow<Int> = _sidebarWidthDp.asStateFlow()

    init {
        scope.launch { loadAll() }
    }

    private suspend fun loadAll() {
        _densityScale.value = settings.getDensityScale()
        _fontScale.value = settings.getFontScale()
        _animationSpeed.value = safeEnum(settings.getAnimationSpeed(), AnimationSpeed.Normal)
        _defaultViewMode.value = safeEnum(settings.getDefaultViewMode(), CharactersViewMode.List)
        _messageWidth.value = safeEnum(settings.getMessageWidth(), MessageWidth.Normal)
        _composerMaxHeightDp.value = settings.getComposerMaxHeight()
        _sidebarWidthDp.value = settings.getSidebarWidth()
        ForgeAnimation.setSpeedMultiplier(_animationSpeed.value.durationMultiplier)
    }

    fun applyState(state: SettingsState) {
        _densityScale.value = state.densityScale
        _fontScale.value = state.fontScale
        _animationSpeed.value = state.animationSpeed
        _defaultViewMode.value = state.defaultViewMode
        _messageWidth.value = state.messageWidth
        _composerMaxHeightDp.value = state.composerMaxHeightDp
        _sidebarWidthDp.value = state.sidebarWidthDp
        ForgeAnimation.setSpeedMultiplier(state.animationSpeed.durationMultiplier)
    }

    private inline fun <reified T : Enum<T>> safeEnum(name: String, default: T): T =
        try { enumValueOf<T>(name) } catch (_: IllegalArgumentException) { default }
}
