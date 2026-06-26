package io.github.dumbgreenfish.dialogueforge.ui.common

enum class CharacterFileType(val extension: String, val mimeTypes: List<String>) {
    Png(".png", listOf("image/png")),
    Json(".json", listOf("application/json")),
    Charx(".charx", listOf("application/zip", "application/octet-stream")),
}