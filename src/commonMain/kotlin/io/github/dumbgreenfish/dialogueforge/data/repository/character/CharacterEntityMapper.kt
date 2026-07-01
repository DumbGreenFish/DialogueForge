package io.github.dumbgreenfish.dialogueforge.data.repository.character

import io.github.dumbgreenfish.dialogueforge.data.model.TavernCardData
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
internal fun TavernCardData.toEntity(): CharacterEntity = CharacterEntity(
    id = Uuid.random().toString(),
    name = name,
    description = description,
    creator = creator,
    avatarData = avatarBytes,
    tags = tags,
    specVersion = specVersion,
    pinned = false,
    chatCount = 0,
    firstMessage = firstMessage,
    importedAt = Clock.System.now().toEpochMilliseconds(),
    lastUsedAt = null,
)
