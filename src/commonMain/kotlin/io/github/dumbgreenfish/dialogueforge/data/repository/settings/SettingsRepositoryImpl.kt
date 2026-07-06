package io.github.dumbgreenfish.dialogueforge.data.repository.settings

import io.github.dumbgreenfish.dialogueforge.data.config.DatabaseConfig
import org.koin.core.annotation.Single

@Single(binds = [SettingsRepository::class])
class SettingsRepositoryImpl(dbConfig: DatabaseConfig) : SettingsRepository {

    private val dao = dbConfig.mainDatabase().settingDao()

    override suspend fun get(key: String): String? = dao.get(key)

    override suspend fun set(key: String, value: String) {
        dao.set(SettingEntity(key = key, value = value))
    }

    override suspend fun getApiKey(): String? = get("api_key")

    override suspend fun setApiKey(key: String) = set("api_key", key)

    override suspend fun getEndpoint(): String =
        get("endpoint") ?: SettingsRepository.DEFAULT_ENDPOINT

    override suspend fun setEndpoint(endpoint: String) = set("endpoint", endpoint)

    override suspend fun getModel(): String =
        get("model") ?: SettingsRepository.DEFAULT_MODEL

    override suspend fun setModel(model: String) = set("model", model)

    override suspend fun getTemperature(): Float =
        get("temperature")?.toFloatOrNull() ?: SettingsRepository.DEFAULT_TEMPERATURE

    override suspend fun setTemperature(temp: Float) = set("temperature", temp.toString())

    override suspend fun getMaxTokens(): Int =
        get("max_tokens")?.toIntOrNull() ?: SettingsRepository.DEFAULT_MAX_TOKENS

    override suspend fun setMaxTokens(tokens: Int) = set("max_tokens", tokens.toString())

    override suspend fun getDensityScale(): Float =
        get("density_scale")?.toFloatOrNull() ?: SettingsRepository.DEFAULT_DENSITY_SCALE

    override suspend fun setDensityScale(value: Float) = set("density_scale", value.toString())

    override suspend fun getFontScale(): Float =
        get("font_scale")?.toFloatOrNull() ?: SettingsRepository.DEFAULT_FONT_SCALE

    override suspend fun setFontScale(value: Float) = set("font_scale", value.toString())

    override suspend fun getAnimationSpeed(): String =
        get("animation_speed") ?: SettingsRepository.DEFAULT_ANIMATION_SPEED

    override suspend fun setAnimationSpeed(value: String) = set("animation_speed", value)

    override suspend fun getDefaultViewMode(): String =
        get("default_view_mode") ?: SettingsRepository.DEFAULT_VIEW_MODE

    override suspend fun setDefaultViewMode(value: String) = set("default_view_mode", value)

    override suspend fun getMessageWidth(): String =
        get("message_width") ?: SettingsRepository.DEFAULT_MESSAGE_WIDTH

    override suspend fun setMessageWidth(value: String) = set("message_width", value)

    override suspend fun getComposerMaxHeight(): Int =
        get("composer_max_height")?.toIntOrNull() ?: SettingsRepository.DEFAULT_COMPOSER_MAX_HEIGHT

    override suspend fun setComposerMaxHeight(value: Int) = set("composer_max_height", value.toString())

    override suspend fun getSidebarWidth(): Int =
        get("sidebar_width")?.toIntOrNull() ?: SettingsRepository.DEFAULT_SIDEBAR_WIDTH

    override suspend fun setSidebarWidth(value: Int) = set("sidebar_width", value.toString())
}
