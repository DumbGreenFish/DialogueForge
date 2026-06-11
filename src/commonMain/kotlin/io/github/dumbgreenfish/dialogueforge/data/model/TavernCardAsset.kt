package io.github.dumbgreenfish.dialogueforge.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TavernCardAsset(
    val type: String,
    val uri: String,
    val name: String,
    val ext: String,
)
