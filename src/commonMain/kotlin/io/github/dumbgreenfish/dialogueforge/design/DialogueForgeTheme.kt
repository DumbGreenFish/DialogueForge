package io.github.dumbgreenfish.dialogueforge.design

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun DialogueForgeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ForgeColorScheme,
        typography  = ForgeTypography,
        shapes      = ForgeShapes,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurface,
            content = content,
        )
    }
}
