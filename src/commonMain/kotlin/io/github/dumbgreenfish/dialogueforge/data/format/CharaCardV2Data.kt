package io.github.dumbgreenfish.dialogueforge.data.format

import io.github.dumbgreenfish.dialogueforge.data.model.CharacterBook
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class CharaCardV2Data(
    val name: String = "",
    val description: String = "",
    val personality: String = "",
    val scenario: String = "",
    @SerialName("first_mes") val firstMes: String = "",
    @SerialName("mes_example") val mesExample: String = "",
    @SerialName("creator_notes") val creatorNotes: String = "",
    @SerialName("system_prompt") val systemPrompt: String = "",
    @SerialName("post_history_instructions") val postHistoryInstructions: String = "",
    @SerialName("alternate_greetings") val alternateGreetings: List<String> = emptyList(),
    @SerialName("character_book") val characterBook: CharacterBook? = null,
    val tags: List<String> = emptyList(),
    val creator: String = "",
    @SerialName("character_version") val characterVersion: String = "",
    val extensions: JsonObject = JsonObject(emptyMap()),
)
