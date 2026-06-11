package io.github.dumbgreenfish.dialogueforge.data.format

import io.github.dumbgreenfish.dialogueforge.data.model.TavernCardData

sealed class ParseResult {
    data class Success(val data: TavernCardData) : ParseResult()
    data class Failure(val message: String) : ParseResult()
}
