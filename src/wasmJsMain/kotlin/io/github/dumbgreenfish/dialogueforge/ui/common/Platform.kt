package io.github.dumbgreenfish.dialogueforge.ui.common

actual val isMobilePlatform: Boolean = js("matchMedia('(any-pointer: coarse)').matches")
actual val isDesktopPlatform: Boolean = !isMobilePlatform
