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

    companion object {
        const val DEFAULT_ENDPOINT = "https://api.deepseek.com/chat/completions"
        const val DEFAULT_MODEL = "deepseek-chat"
        const val DEFAULT_TEMPERATURE = 0.7f
        const val DEFAULT_MAX_TOKENS = 4096
    }
}
