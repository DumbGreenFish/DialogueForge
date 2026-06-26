package io.github.dumbgreenfish.dialogueforge.ui.characters.model

import kotlin.jvm.JvmInline

@JvmInline
value class Tag private constructor(val value: String) {
    companion object {
        operator fun invoke(raw: String): Tag {
            return Tag(raw.trim().lowercase())
        }
    }
}