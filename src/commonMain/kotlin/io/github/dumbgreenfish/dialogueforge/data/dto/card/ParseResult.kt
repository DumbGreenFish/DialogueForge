package io.github.dumbgreenfish.dialogueforge.data.dto.card

sealed class ParseResult {
    data class Success(val data: TavernCardData) : ParseResult()
    data class Failure(val message: String) : ParseResult()
}
