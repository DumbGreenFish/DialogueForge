package io.github.dumbgreenfish.dialogueforge.ui.settings.model

enum class MessageWidth(
    val compactFraction: Float,
    val desktopFraction: Float,
    val desktopMaxWidthDp: Int,
) {
    Compact(
        compactFraction = 0.90f,
        desktopFraction = 0.84f,
        desktopMaxWidthDp = 500,
    ),
    Normal(
        compactFraction = 0.84f,
        desktopFraction = 0.76f,
        desktopMaxWidthDp = 588,
    ),
    Wide(
        compactFraction = 0.92f,
        desktopFraction = 0.88f,
        desktopMaxWidthDp = 700,
    ),
}
