package io.github.dumbgreenfish.dialogueforge.ui.common

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun ByteArray.toImageBitmapOrNull(): ImageBitmap? = runCatching {
    BitmapFactory.decodeByteArray(this, 0, size)?.asImageBitmap()
}.getOrNull()
