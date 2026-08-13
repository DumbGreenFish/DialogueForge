package io.github.dumbgreenfish.dialogueforge.testing

import io.github.dumbgreenfish.dialogueforge.data.repository.settings.SettingsRepository

internal class FakeSettingsRepository(
    private var apiKey: String? = "test-api-key",
    private var endpoint: String = "https://example.test/chat/completions",
    private var model: String = "test-model",
    private var temperature: Float = 0.7f,
    private var maxTokens: Int = 4096,
) : SettingsRepository {
    private val values = mutableMapOf<String, String>()
    private var densityScale = SettingsRepository.DEFAULT_DENSITY_SCALE
    private var fontScale = SettingsRepository.DEFAULT_FONT_SCALE
    private var animationSpeed = SettingsRepository.DEFAULT_ANIMATION_SPEED
    private var defaultViewMode = SettingsRepository.DEFAULT_VIEW_MODE
    private var messageWidth = SettingsRepository.DEFAULT_MESSAGE_WIDTH
    private var composerMaxHeight = SettingsRepository.DEFAULT_COMPOSER_MAX_HEIGHT
    private var sidebarWidth = SettingsRepository.DEFAULT_SIDEBAR_WIDTH
    private var chatBackgroundBytes: ByteArray? = null
    private var chatBackgroundOpacity = SettingsRepository.DEFAULT_CHAT_BACKGROUND_OPACITY
    private var chatHeaderOpacity = SettingsRepository.DEFAULT_CHAT_HEADER_OPACITY
    private var chatComposerOpacity = SettingsRepository.DEFAULT_CHAT_COMPOSER_OPACITY
    private var chatBackgroundDim = SettingsRepository.DEFAULT_CHAT_BACKGROUND_DIM
    private var hasCompletedFirstLaunch = false
    private var airiVersion = 0

    override suspend fun get(key: String): String? = values[key]
    override suspend fun set(key: String, value: String) { values[key] = value }
    override suspend fun getApiKey(): String? = apiKey
    override suspend fun setApiKey(key: String) { apiKey = key }
    override suspend fun getEndpoint(): String = endpoint
    override suspend fun setEndpoint(endpoint: String) { this.endpoint = endpoint }
    override suspend fun getModel(): String = model
    override suspend fun setModel(model: String) { this.model = model }
    override suspend fun getTemperature(): Float = temperature
    override suspend fun setTemperature(temp: Float) { temperature = temp }
    override suspend fun getMaxTokens(): Int = maxTokens
    override suspend fun setMaxTokens(tokens: Int) { maxTokens = tokens }
    override suspend fun getDensityScale(): Float = densityScale
    override suspend fun setDensityScale(value: Float) { densityScale = value }
    override suspend fun getFontScale(): Float = fontScale
    override suspend fun setFontScale(value: Float) { fontScale = value }
    override suspend fun getAnimationSpeed(): String = animationSpeed
    override suspend fun setAnimationSpeed(value: String) { animationSpeed = value }
    override suspend fun getDefaultViewMode(): String = defaultViewMode
    override suspend fun setDefaultViewMode(value: String) { defaultViewMode = value }
    override suspend fun getMessageWidth(): String = messageWidth
    override suspend fun setMessageWidth(value: String) { messageWidth = value }
    override suspend fun getComposerMaxHeight(): Int = composerMaxHeight
    override suspend fun setComposerMaxHeight(value: Int) { composerMaxHeight = value }
    override suspend fun getSidebarWidth(): Int = sidebarWidth
    override suspend fun setSidebarWidth(value: Int) { sidebarWidth = value }
    override suspend fun getChatBackgroundBytes(): ByteArray? = chatBackgroundBytes
    override suspend fun setChatBackgroundBytes(bytes: ByteArray?) { chatBackgroundBytes = bytes }
    override suspend fun getChatBackgroundOpacity(): Float = chatBackgroundOpacity
    override suspend fun setChatBackgroundOpacity(value: Float) { chatBackgroundOpacity = value }
    override suspend fun getChatHeaderOpacity(): Float = chatHeaderOpacity
    override suspend fun setChatHeaderOpacity(value: Float) { chatHeaderOpacity = value }
    override suspend fun getChatComposerOpacity(): Float = chatComposerOpacity
    override suspend fun setChatComposerOpacity(value: Float) { chatComposerOpacity = value }
    override suspend fun getChatBackgroundDim(): Float = chatBackgroundDim
    override suspend fun setChatBackgroundDim(value: Float) { chatBackgroundDim = value }
    override suspend fun getHasCompletedFirstLaunch(): Boolean = hasCompletedFirstLaunch
    override suspend fun setHasCompletedFirstLaunch(value: Boolean) { hasCompletedFirstLaunch = value }
    override suspend fun getAiriVersion(): Int = airiVersion
    override suspend fun setAiriVersion(value: Int) { airiVersion = value }
}
