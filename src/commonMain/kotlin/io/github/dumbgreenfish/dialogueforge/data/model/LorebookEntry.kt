package io.github.dumbgreenfish.dialogueforge.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class LorebookEntry(
    val keys: List<String> = emptyList(),
    val content: String = "",
    val extensions: JsonObject = JsonObject(emptyMap()),
    val enabled: Boolean = true,
    @SerialName("insertion_order") val insertionOrder: Int = 0,
    @SerialName("case_sensitive") val caseSensitive: Boolean? = null,
    @SerialName("use_regex") val useRegex: Boolean = false,
    val constant: Boolean? = null,
    val name: String? = null,
    val priority: Int? = null,
    val id: JsonPrimitive? = null,
    val comment: String? = null,
    val selective: Boolean? = null,
    @SerialName("secondary_keys") val secondaryKeys: List<String>? = null,
    val position: String? = null,
)
