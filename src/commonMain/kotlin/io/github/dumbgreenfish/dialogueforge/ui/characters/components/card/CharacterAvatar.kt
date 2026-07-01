package io.github.dumbgreenfish.dialogueforge.ui.characters.components.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.ui.common.toImageBitmapOrNull

@Composable
internal fun CharacterAvatar(
    letter: String,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = ForgeShape.avatar,
    fontSize: TextUnit = 24.sp,
    avatarBytes: ByteArray? = null,
) {
    val cs = MaterialTheme.colorScheme
    val image = remember(avatarBytes) { avatarBytes?.toImageBitmapOrNull() }
    Box(
        modifier = modifier.clip(shape).background(cs.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        if (image != null) {
            Image(
                bitmap = image,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = letter.take(1),
                color = cs.onPrimaryContainer,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
