package io.github.dumbgreenfish.dialogueforge.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class CharacterBook(
    val name: String? = null,
    val description: String? = null,
    @SerialName("scan_depth") val scanDepth: Int? = null,
    @SerialName("token_budget") val tokenBudget: Int? = null,
    @SerialName("recursive_scanning") val recursiveScanning: Boolean? = null,
    val extensions: JsonObject = JsonObject(emptyMap()),
    val entries: List<LorebookEntry> = emptyList(),
)
