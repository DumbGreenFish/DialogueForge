package io.github.dumbgreenfish.dialogueforge.data.format

import io.github.dumbgreenfish.dialogueforge.data.model.TavernCardData
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private val CardJson = Json { ignoreUnknownKeys = true }

private val PngHeader = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)
private val TExtType  = byteArrayOf(0x74, 0x45, 0x58, 0x74)

object TavernCardParser {

    fun parse(bytes: ByteArray, filename: String): ParseResult {
        if (bytes.size >= 2 && bytes[0] == 0x50.toByte() && bytes[1] == 0x4B.toByte())
            return ParseResult.Failure("CHARX format is not yet supported")
        if (bytes.startsWith(PngHeader)) return parsePng(bytes)
        val result = parseJsonBytes(bytes, avatarBytes = null)
        if (result is ParseResult.Success && result.data.avatarBytes == null)
            return ParseResult.Failure("JSON character card has no avatar image")
        return result
    }

    private fun parsePng(bytes: ByteArray): ParseResult {
        var ccv3Text: String? = null
        var charaText: String? = null
        var offset = 8

        while (offset + 12 <= bytes.size) {
            val length = bytes.readInt32BE(offset)
            if (length < 0 || offset + 12 + length > bytes.size) break
            val typeBytes = bytes.copyOfRange(offset + 4, offset + 8)
            val data      = bytes.copyOfRange(offset + 8, offset + 8 + length)
            offset += 12 + length

            if (!typeBytes.contentEquals(TExtType)) continue

            val nullPos = data.indexOf(0.toByte())
            if (nullPos < 0) continue
            val keyword = data.copyOfRange(0, nullPos).decodeToString()
            val value   = data.copyOfRange(nullPos + 1, data.size).decodeToString()

            when (keyword) {
                "ccv3"  -> ccv3Text = value
                "chara" -> charaText = value
            }
        }

        val text = ccv3Text ?: charaText
            ?: return ParseResult.Failure("No character data found in PNG file")
        return parseBase64Json(text, avatarBytes = bytes)
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun parseBase64Json(base64: String, avatarBytes: ByteArray?): ParseResult {
        val jsonBytes = try {
            Base64.decode(base64.trim())
        } catch (e: Exception) {
            return ParseResult.Failure("Failed to decode base64: ${e.message}")
        }
        return parseJsonBytes(jsonBytes, avatarBytes)
    }

    private fun parseJsonBytes(bytes: ByteArray, avatarBytes: ByteArray?): ParseResult =
        parseJsonString(bytes.decodeToString(), avatarBytes)

    private fun parseJsonString(jsonString: String, avatarBytes: ByteArray?): ParseResult = try {
        val root: JsonObject = CardJson.parseToJsonElement(jsonString).jsonObject
        val spec = root["spec"]?.jsonPrimitive?.content
        when (spec) {
            "chara_card_v3" -> parseV3(root, avatarBytes)
            "chara_card_v2" -> parseV2(root, avatarBytes)
            else            -> parseV1(root, avatarBytes)
        }
    } catch (e: Exception) {
        ParseResult.Failure("Failed to parse JSON: ${e.message}")
    }

    private fun parseV3(root: JsonObject, avatarBytes: ByteArray?): ParseResult {
        val dataJson = root["data"]?.jsonObject
            ?: return ParseResult.Failure("V3 card missing 'data' field")
        return try {
            val d = CardJson.decodeFromJsonElement<CharaCardV3Data>(dataJson)
            ParseResult.Success(TavernCardData(
                name = d.name, description = d.description, personality = d.personality,
                scenario = d.scenario, firstMessage = d.firstMes, exampleMessages = d.mesExample,
                creatorNotes = d.creatorNotes, systemPrompt = d.systemPrompt,
                postHistoryInstructions = d.postHistoryInstructions,
                alternateGreetings = d.alternateGreetings, characterBook = d.characterBook,
                tags = d.tags, creator = d.creator, characterVersion = d.characterVersion,
                extensions = d.extensions, assets = d.assets ?: emptyList(),
                nickname = d.nickname, creatorNotesMultilingual = d.creatorNotesMultilingual,
                source = d.source ?: emptyList(), groupOnlyGreetings = d.groupOnlyGreetings,
                creationDate = d.creationDate, modificationDate = d.modificationDate,
                specVersion = "3.0", avatarBytes = avatarBytes,
            ))
        } catch (e: Exception) {
            ParseResult.Failure("Failed to parse V3 data: ${e.message}")
        }
    }

    private fun parseV2(root: JsonObject, avatarBytes: ByteArray?): ParseResult {
        val dataJson = root["data"]?.jsonObject
            ?: return ParseResult.Failure("V2 card missing 'data' field")
        return try {
            val d = CardJson.decodeFromJsonElement<CharaCardV2Data>(dataJson)
            ParseResult.Success(TavernCardData(
                name = d.name, description = d.description, personality = d.personality,
                scenario = d.scenario, firstMessage = d.firstMes, exampleMessages = d.mesExample,
                creatorNotes = d.creatorNotes, systemPrompt = d.systemPrompt,
                postHistoryInstructions = d.postHistoryInstructions,
                alternateGreetings = d.alternateGreetings, characterBook = d.characterBook,
                tags = d.tags, creator = d.creator, characterVersion = d.characterVersion,
                extensions = d.extensions, specVersion = "2.0", avatarBytes = avatarBytes,
            ))
        } catch (e: Exception) {
            ParseResult.Failure("Failed to parse V2 data: ${e.message}")
        }
    }

    private fun parseV1(root: JsonObject, avatarBytes: ByteArray?): ParseResult = try {
        val d = CardJson.decodeFromJsonElement<CharaCardV1>(root)
        ParseResult.Success(TavernCardData(
            name = d.name, description = d.description, personality = d.personality,
            scenario = d.scenario, firstMessage = d.firstMes, exampleMessages = d.mesExample,
            specVersion = "1.0", avatarBytes = avatarBytes,
        ))
    } catch (e: Exception) {
        ParseResult.Failure("Failed to parse V1 data: ${e.message}")
    }
}

private fun ByteArray.startsWith(prefix: ByteArray): Boolean {
    if (size < prefix.size) return false
    return prefix.indices.all { this[it] == prefix[it] }
}

private fun ByteArray.readInt32BE(offset: Int): Int =
    ((this[offset].toInt() and 0xFF) shl 24) or
    ((this[offset + 1].toInt() and 0xFF) shl 16) or
    ((this[offset + 2].toInt() and 0xFF) shl 8) or
    (this[offset + 3].toInt() and 0xFF)
