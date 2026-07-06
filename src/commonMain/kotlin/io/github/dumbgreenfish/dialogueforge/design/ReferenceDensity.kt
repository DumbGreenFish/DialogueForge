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
fun WithReferenceDensity(
    densityScale: Float = 1.0f,
    fontScale: Float = 1.0f,
    content: @Composable () -> Unit,
) {
    val pixelHeight = LocalWindowInfo.current.containerSize.height
    val baseDensity = (pixelHeight / REFERENCE_HEIGHT_PX).coerceIn(MIN_SCALE, MAX_SCALE)
    val density = baseDensity * densityScale
    val systemFontScale = LocalDensity.current.fontScale
    CompositionLocalProvider(
        LocalDensity provides Density(density = density, fontScale = systemFontScale * fontScale),
        content = content,
    )
}
