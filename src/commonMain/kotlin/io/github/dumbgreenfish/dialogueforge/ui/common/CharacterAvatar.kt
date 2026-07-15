package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape

@Composable
internal fun CharacterAvatar(
    imageProvider: ImageProvider,
    targetSizeDp: Dp,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = ForgeShape.avatar,
) {
    val density = LocalDensity.current.density
    val maxDimension = remember(targetSizeDp, density) {
        if (targetSizeDp > 0.dp) (targetSizeDp.value * density * 1.5f).toInt() else 0
    }
    val bitmap by imageProvider.observe(maxDimension).collectAsState()

    Box(modifier = modifier.clip(shape), contentAlignment = Alignment.Center) {
        bitmap?.let { bmp ->
            Image(
                bitmap = bmp,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.High,
            )
        }
    }
}
