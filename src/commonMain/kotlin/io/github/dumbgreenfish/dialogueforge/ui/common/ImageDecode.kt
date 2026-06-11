package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.ui.graphics.ImageBitmap

expect fun ByteArray.toImageBitmapOrNull(): ImageBitmap?
