package io.github.dumbgreenfish.dialogueforge.data.repository.settings

interface SettingsRepository {
    suspend fun get(key: String): String?
    suspend fun set(key: String, value: String)

    suspend fun getApiKey(): String?
    suspend fun setApiKey(key: String)

    suspend fun getEndpoint(): String
    suspend fun setEndpoint(endpoint: String)

    suspend fun getModel(): String
    suspend fun setModel(model: String)

    suspend fun getTemperature(): Float
    suspend fun setTemperature(temp: Float)

    suspend fun getMaxTokens(): Int
    suspend fun setMaxTokens(tokens: Int)

    suspend fun getDensityScale(): Float
    suspend fun setDensityScale(value: Float)

    suspend fun getFontScale(): Float
    suspend fun setFontScale(value: Float)

    suspend fun getAnimationSpeed(): String
    suspend fun setAnimationSpeed(value: String)

    suspend fun getDefaultViewMode(): String
    suspend fun setDefaultViewMode(value: String)

    suspend fun getMessageWidth(): String
    suspend fun setMessageWidth(value: String)

    suspend fun getComposerMaxHeight(): Int
    suspend fun setComposerMaxHeight(value: Int)

    suspend fun getSidebarWidth(): Int
    suspend fun setSidebarWidth(value: Int)

    companion object {
        const val DEFAULT_ENDPOINT = "https://api.deepseek.com/chat/completions"
        const val DEFAULT_MODEL = "deepseek-chat"
        const val DEFAULT_TEMPERATURE = 0.7f
        const val DEFAULT_MAX_TOKENS = 4096

        const val DEFAULT_DENSITY_SCALE = 1.0f
        const val DEFAULT_FONT_SCALE = 1.0f
        const val DEFAULT_ANIMATION_SPEED = "Normal"
        const val DEFAULT_VIEW_MODE = "List"
        const val DEFAULT_MESSAGE_WIDTH = "Normal"
        const val DEFAULT_COMPOSER_MAX_HEIGHT = 160
        const val DEFAULT_SIDEBAR_WIDTH = 240
    }
}
