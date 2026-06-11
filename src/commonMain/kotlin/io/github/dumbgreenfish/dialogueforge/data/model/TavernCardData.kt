package io.github.dumbgreenfish.dialogueforge.data.model

import kotlinx.serialization.json.JsonObject

data class TavernCardData(
    val name: String,
    val description: String,
    val personality: String,
    val scenario: String,
    val firstMessage: String,
    val exampleMessages: String,
    val creatorNotes: String = "",
    val systemPrompt: String = "",
    val postHistoryInstructions: String = "",
    val alternateGreetings: List<String> = emptyList(),
    val characterBook: CharacterBook? = null,
    val tags: List<String> = emptyList(),
    val creator: String = "",
    val characterVersion: String = "",
    val extensions: JsonObject = JsonObject(emptyMap()),
    val assets: List<TavernCardAsset> = emptyList(),
    val nickname: String? = null,
    val creatorNotesMultilingual: Map<String, String>? = null,
    val source: List<String> = emptyList(),
    val groupOnlyGreetings: List<String> = emptyList(),
    val creationDate: Long? = null,
    val modificationDate: Long? = null,
    val specVersion: String,
    val avatarBytes: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TavernCardData) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    private val id: String get() = "$specVersion:$name"
}
