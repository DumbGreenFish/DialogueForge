package io.github.dumbgreenfish.dialogueforge.design

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

private val xxs = RoundedCornerShape(6.dp)
private val xs  = RoundedCornerShape(8.dp)
private val sm  = RoundedCornerShape(10.dp)
private val md  = RoundedCornerShape(12.dp)
private val lg  = RoundedCornerShape(14.dp)
private val xl  = RoundedCornerShape(20.dp)

val forgeShapes = Shapes(
    extraSmall = xxs,
    small      = sm,
    medium     = md,
    large      = lg,
    extraLarge = xl,
)

object ForgeShapes {
    val pill = RoundedCornerShape(100.dp)
    val circle = CircleShape

    val hero = xs
    val card = xxs
    val avatar = xxs

    val pinnedBadge = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomEnd = 4.dp,
        bottomStart = 4.dp,
    )

    val modelPill = sm
    val userRow = md
    val footer  = md
}