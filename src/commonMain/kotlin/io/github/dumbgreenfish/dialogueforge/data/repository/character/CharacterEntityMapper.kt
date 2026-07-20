package io.github.dumbgreenfish.dialogueforge.data.repository.character

import io.github.dumbgreenfish.dialogueforge.data.model.TavernCardData
import io.github.dumbgreenfish.dialogueforge.util.image.generateThumbnail
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
internal fun TavernCardData.toEntity(): CharacterEntity {
    val now = Clock.System.now().toEpochMilliseconds()
    val fullAvatar = requireNotNull(avatarBytes) { "Character must have avatar data" }
    return CharacterEntity(
        id = Uuid.random().toString(),
        name = name,
        description = description,
        creator = creator,
        avatarData = fullAvatar,
        mainImageThumbnailData = fullAvatar.generateThumbnail(512),
        thumbnailSmall = fullAvatar.generateThumbnail(128),
        thumbnailMedium = fullAvatar.generateThumbnail(224),
        thumbnailLarge = fullAvatar.generateThumbnail(288),
        tags = tags,
        specVersion = specVersion,
        pinned = false,
        chatCount = 0,
        firstMessage = firstMessage,
        personality = personality,
        scenario = scenario,
        importedAt = now,
        updatedAt = now,
        lastUsedAt = null,
    )
}
