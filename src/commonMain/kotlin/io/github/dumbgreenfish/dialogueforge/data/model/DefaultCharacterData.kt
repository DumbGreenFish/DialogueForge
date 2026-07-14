package io.github.dumbgreenfish.dialogueforge.data.model

import io.github.dumbgreenfish.dialogueforge.data.format.ParseResult
import io.github.dumbgreenfish.dialogueforge.data.format.TavernCardParser
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

object DefaultCharacterData {

    @OptIn(ExperimentalResourceApi::class)
    suspend fun create(): TavernCardData? {
        val cardBytes = Res.readBytes("files/airi_card.json")
        val avatarBytes = try {
            Res.readBytes("files/airi_avatar.png")
        } catch (_: Exception) { null }

        val result = TavernCardParser.parse(cardBytes, "airi_card.json")
        return when (result) {
            is ParseResult.Success -> result.data.copy(avatarBytes = avatarBytes)
            is ParseResult.Failure -> null
        }
    }
}
