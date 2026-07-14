package io.github.dumbgreenfish.dialogueforge.ui.dialogue.model

data class Message(
    val id: String,
    val role: MessageRole,
    val text: String,
    val timestamp: Long,
    val variantIndex: Int = 0,
    val variantCount: Int = 1,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Message) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
