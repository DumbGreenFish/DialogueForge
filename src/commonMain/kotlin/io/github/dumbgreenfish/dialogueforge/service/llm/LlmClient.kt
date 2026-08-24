package io.github.dumbgreenfish.dialogueforge.service.llm

interface LlmClient {
    suspend fun chat(
        systemPrompt: String,
        history: List<Pair<String, String>>,
        onUpdate: suspend (String) -> Unit = {},
    ): Result<String>
}
