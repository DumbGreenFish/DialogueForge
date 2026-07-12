package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.ui.common.toImageBitmapOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val DefaultAvatarFontSize = 24.sp

@Composable
internal fun CharacterAvatar(
    letter: String,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = ForgeShape.avatar,
    fontSize: TextUnit = DefaultAvatarFontSize,
    avatarBytes: ByteArray? = null,
) {
    val cs = MaterialTheme.colorScheme
    val image by produceState<ImageBitmap?>(initialValue = null, avatarBytes) {
        value = avatarBytes?.let { bytes -> withContext(Dispatchers.Default) { bytes.toImageBitmapOrNull() } }
    }
    Box(
        modifier = modifier.clip(shape).background(cs.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        val bitmap = image
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
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
