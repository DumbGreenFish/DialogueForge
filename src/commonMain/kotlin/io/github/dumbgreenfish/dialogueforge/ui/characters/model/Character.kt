package io.github.dumbgreenfish.dialogueforge.ui.characters.model

data class Character(
    val id: String,
    val name: String,
    val letter: String,
    val tagline: String,
    val tags: List<String>,
    val chats: Int,
    val lastUsed: String,
    val pinned: Boolean,
    val source: String,
)
