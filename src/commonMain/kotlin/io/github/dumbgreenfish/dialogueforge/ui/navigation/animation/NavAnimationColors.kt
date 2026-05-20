package io.github.dumbgreenfish.dialogueforge.ui.navigation.animation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

internal data class NavAnimationColors(
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
)

@Composable
internal fun navAnimationColors() = NavAnimationColors(
    primaryContainer   = MaterialTheme.colorScheme.primaryContainer,
    onPrimaryContainer = MaterialTheme.colorScheme.onPrimaryContainer,
    onSurface          = MaterialTheme.colorScheme.onSurface,
    onSurfaceVariant   = MaterialTheme.colorScheme.onSurfaceVariant,
)
