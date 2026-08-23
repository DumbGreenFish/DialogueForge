package io.github.dumbgreenfish.dialogueforge.service.generation

internal fun shouldPostCompletionNotification(
    appVisible: Boolean,
    visibleCharacterId: String?,
    completedCharacterId: String,
): Boolean = !appVisible || visibleCharacterId != completedCharacterId
