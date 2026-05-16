package io.github.dumbgreenfish.dialogueforge.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun DialogueForgeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ForgeColorScheme,
        typography  = ForgeTypography,
        shapes      = ForgeShapes,
        content     = content,
    )
}
