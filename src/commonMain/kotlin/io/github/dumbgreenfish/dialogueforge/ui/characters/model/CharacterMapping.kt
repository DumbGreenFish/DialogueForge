package io.github.dumbgreenfish.dialogueforge.ui.characters.model

import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterEntity
import io.github.dumbgreenfish.dialogueforge.ui.common.formatDate

private const val TAGLINE_PREVIEW_LENGTH = 120
private val CHAR_PLACEHOLDERS = listOf("{{char}}", "<char>", "<bot>")

private fun String.substituteCharName(name: String): String =
    CHAR_PLACEHOLDERS.fold(this) { current, placeholder ->
        current.replace(placeholder, name, ignoreCase = true)
    }

fun CharacterEntity.toCharacter(): Character = Character(
    id = id,
    name = name,

    tagline = description.substituteCharName(name).take(TAGLINE_PREVIEW_LENGTH),
    description = description,
    tags = tags.map { Tag(it) },
    chats = chatCount,
    lastUsed = lastUsedAt?.let { formatDate(it) } ?: "",
    pinned = pinned,
    source = creator,
    firstMessage = firstMessage,
    personality = personality,
    scenario = scenario,
    updatedAt = updatedAt,
)
