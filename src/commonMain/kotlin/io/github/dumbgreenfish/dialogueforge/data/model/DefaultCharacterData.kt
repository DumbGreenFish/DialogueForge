package io.github.dumbgreenfish.dialogueforge.data.model

import io.github.dumbgreenfish.dialogueforge.data.format.ParseResult
import io.github.dumbgreenfish.dialogueforge.data.format.TavernCardParser
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

object DefaultCharacterData {

    const val AIRI_VERSION = 2

    @OptIn(ExperimentalResourceApi::class)
    suspend fun create(): TavernCardData? {
        val cardBytes = Res.readBytes("files/airi_card.json")
        val avatarBytes = try {
            Res.readBytes("files/airi_avatar.png")
        } catch (_: Exception) { null }

        val result = TavernCardParser.parse(cardBytes, "airi_card.json", externalAvatar = avatarBytes)
        return when (result) {
            is ParseResult.Success -> result.data
            is ParseResult.Failure -> null
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    suspend fun createDebug(): TavernCardData? {
        val cardBytes = Res.readBytes("files/sasha_card.json")
        val avatarBytes = try {
            Res.readBytes("files/sasha_avatar.png")
        } catch (_: Exception) { null }

        val result = TavernCardParser.parse(cardBytes, "sasha_card.json", externalAvatar = avatarBytes)
        return when (result) {
            is ParseResult.Success -> result.data
            is ParseResult.Failure -> null
        }
    }
}
