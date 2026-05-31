package io.github.dumbgreenfish.dialogueforge.design

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Density

private const val REFERENCE_HEIGHT_PX = 720f
private const val MIN_SCALE = 0.7f
private const val MAX_SCALE = 3f

@Composable
fun WithReferenceDensity(content: @Composable () -> Unit) {
    val pixelHeight = LocalWindowInfo.current.containerSize.height
    val scale = (pixelHeight / REFERENCE_HEIGHT_PX).coerceIn(MIN_SCALE, MAX_SCALE)
    val fontScale = LocalDensity.current.fontScale
    CompositionLocalProvider(
        LocalDensity provides Density(density = scale, fontScale = fontScale),
        content = content,
    )
}
