package io.github.dumbgreenfish.dialogueforge.ui.characters.model

data class Character(
    val id: String,
    val name: String,
    val tagline: String,
    val description: String = "",
    val tags: List<Tag>,
    val chats: Int,
    val lastUsed: String,
    val pinned: Boolean,
    val source: String,
    val firstMessage: String = "",
    val personality: String = "",
    val scenario: String = "",
    val updatedAt: Long = 0,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Character) return false
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}
