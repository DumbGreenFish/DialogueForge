package io.github.dumbgreenfish.dialogueforge.data.dto.completion

class LlmResponseException(
    val statusCode: Int,
    val statusDescription: String,
    val responseBody: String,
) : Exception(
    buildString {
        append("HTTP ")
        append(statusCode)
        if (statusDescription.isNotBlank()) {
            append(' ')
            append(statusDescription)
        }
        if (responseBody.isNotBlank()) {
            appendLine()
            append(responseBody)
        }
    },
)