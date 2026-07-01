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
}
