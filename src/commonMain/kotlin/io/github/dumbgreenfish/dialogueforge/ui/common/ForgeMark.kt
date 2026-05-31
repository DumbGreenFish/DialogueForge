package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors

@Composable
fun ForgeMark(modifier: Modifier = Modifier.Companion) {
    val ringColor = MaterialTheme.colorScheme.primary
    val sparkCenter = ForgeColors.sparkHot
    val sparkEdge = MaterialTheme.colorScheme.tertiary

    Canvas(modifier = modifier) {
        val side = minOf(size.width, size.height)
        val center = Offset(size.width / 2f, size.height / 2f)
        val strokeWidth = side * (2f / 26f)
        val ringRadius = side / 2f - strokeWidth / 2f
        drawCircle(
            color = ringColor,
            radius = ringRadius,
            center = center,
            style = Stroke(width = strokeWidth),
        )
        val sparkRadius = side * (6f / 26f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(sparkCenter, sparkEdge),
                center = center,
                radius = sparkRadius,
            ),
            radius = sparkRadius,
            center = center,
        )
    }
}