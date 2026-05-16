package io.github.dumbgreenfish.dialogueforge.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

private val Xs = RoundedCornerShape(4.dp)
private val Sm = RoundedCornerShape(6.dp)
private val Md = RoundedCornerShape(10.dp)
private val Lg = RoundedCornerShape(14.dp)
private val Xl = RoundedCornerShape(20.dp)

val ForgeShapes: Shapes = Shapes(
    extraSmall = Xs,
    small      = Sm,
    medium     = Md,
    large      = Lg,
    extraLarge = Xl,
)

object ForgeShape {
    val pill = RoundedCornerShape(100.dp)
    val avatar = RoundedCornerShape(6.dp)

    val bubbleUser = RoundedCornerShape(
        topStart = 14.dp,
        topEnd = 14.dp,
        bottomEnd = 4.dp,
        bottomStart = 14.dp,
    )

    val bubbleAssistant = RoundedCornerShape(
        topStart = 14.dp,
        topEnd = 14.dp,
        bottomEnd = 14.dp,
        bottomStart = 4.dp,
    )
}