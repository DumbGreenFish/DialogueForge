package io.github.dumbgreenfish.dialogueforge.data.service

interface LlmClient {
    suspend fun chat(
        systemPrompt: String,
        history: List<Pair<String, String>>,
        onUpdate: (String) -> Unit = {},
    ): Result<String>
}
