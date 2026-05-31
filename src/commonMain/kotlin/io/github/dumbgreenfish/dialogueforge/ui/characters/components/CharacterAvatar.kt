package io.github.dumbgreenfish.dialogueforge.ui.characters.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape

@Composable
internal fun CharacterAvatar(
    letter: String,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = ForgeShape.avatar,
    fontSize: TextUnit = 24.sp,
) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = modifier.clip(shape).background(cs.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = letter.take(1),
            color = cs.onPrimaryContainer,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
        )
    }
}
