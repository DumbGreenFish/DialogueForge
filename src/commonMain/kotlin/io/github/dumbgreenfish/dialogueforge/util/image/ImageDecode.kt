package io.github.dumbgreenfish.dialogueforge.util.image

import androidx.compose.ui.graphics.ImageBitmap

expect fun ByteArray.toImageBitmapOrNull(maxDimension: Int = 0): ImageBitmap?

expect fun ByteArray.generateThumbnail(maxDimension: Int): ByteArray
