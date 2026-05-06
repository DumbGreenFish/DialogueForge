package io.github.dumbgreenfish.dialogueforge.design

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

private val xs     = RoundedCornerShape(8.dp)
private val sm     = RoundedCornerShape(10.dp)
private val md     = RoundedCornerShape(12.dp)
private val lg     = RoundedCornerShape(16.dp)
private val xl     = RoundedCornerShape(20.dp)

val forgeShapes = Shapes(
    extraSmall = xs,
    small = sm,
    medium = md,
    large = lg,
    extraLarge = xl,
)

object ForgeShapes {
    val pill   = RoundedCornerShape(100.dp)
    val circle = CircleShape
}
