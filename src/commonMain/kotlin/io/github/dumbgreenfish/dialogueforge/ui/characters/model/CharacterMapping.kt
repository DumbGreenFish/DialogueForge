package io.github.dumbgreenfish.dialogueforge.ui.characters.model

import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterEntity
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private const val TAGLINE_PREVIEW_LENGTH = 120
private val CHAR_PLACEHOLDERS = listOf("{{char}}", "<char>", "<bot>")

private fun String.substituteCharName(name: String): String =
    CHAR_PLACEHOLDERS.fold(this) { current, placeholder ->
        current.replace(placeholder, name, ignoreCase = true)
    }

private fun formatTimestamp(ms: Long): String {
    val dt = Instant.fromEpochMilliseconds(ms).toLocalDateTime(TimeZone.currentSystemDefault())
    return dt.date.toString()
}

fun CharacterEntity.toCharacter(): Character = Character(
    id = id,
    name = name,
    letter = name.firstOrNull()?.uppercase() ?: "?",
    tagline = description.substituteCharName(name).take(TAGLINE_PREVIEW_LENGTH),
    description = description,
    tags = tags.map { Tag(it) },
    chats = chatCount,
    lastUsed = lastUsedAt?.let { formatTimestamp(it) } ?: "",
    pinned = pinned,
    source = creator,
    avatarBytes = avatarData,
    firstMessage = firstMessage,
    personality = personality,
    scenario = scenario,
)
