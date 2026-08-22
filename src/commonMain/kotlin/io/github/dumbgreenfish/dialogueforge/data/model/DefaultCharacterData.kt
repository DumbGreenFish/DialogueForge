package io.github.dumbgreenfish.dialogueforge.data.model

import io.github.dumbgreenfish.dialogueforge.data.format.ParseResult
import io.github.dumbgreenfish.dialogueforge.data.format.TavernCardParser
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

object DefaultCharacterData {

    const val AIRI_VERSION = 5

    private const val CURRENT_PERSONALITY = """

Airi is a girl and uses feminine terms when referring to herself.
"""

    private const val CURRENT_KNOWLEDGE = """

Streaming and background generation:
- Standard buffered generation is enabled by default and keeps the established behavior of showing the assistant message after the provider completes it.
- Streaming can be enabled in Chat settings. In that mode, response text appears progressively while it arrives from an OpenAI-compatible provider.
- DialogueForge continues generating after the Android app is minimized. Android uses a foreground notification so the active generation can continue in the background.
- If streaming generation is stopped or fails after some text arrived, the partial response is saved as a normal assistant message. It remains in conversation history and is included normally in later requests.
- Provider errors are shown when possible. A response stopped by a token limit or content filter has a specific error message. Streaming failures are not automatically retried through buffered generation.
- During streaming, the chat follows the newest text until the user scrolls upward. It stays detached while older messages are being read and resumes following after the user returns to the bottom.
"""

    @OptIn(ExperimentalResourceApi::class)
    suspend fun create(): TavernCardData? {
        val cardBytes = Res.readBytes("files/airi_card.json")
        val avatarBytes = try {
            Res.readBytes("files/airi_avatar.png")
        } catch (_: Exception) { null }

        val result = TavernCardParser.parse(cardBytes, "airi_card.json", externalAvatar = avatarBytes)
        return when (result) {
            is ParseResult.Success -> result.data.copy(
                personality = result.data.personality + CURRENT_PERSONALITY,
                creatorNotes = result.data.creatorNotes + CURRENT_KNOWLEDGE,
                characterVersion = AIRI_VERSION.toString(),
            )
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
