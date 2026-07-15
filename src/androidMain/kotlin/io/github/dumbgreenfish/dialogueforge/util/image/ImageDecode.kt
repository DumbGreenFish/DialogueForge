package io.github.dumbgreenfish.dialogueforge.util.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream
import androidx.core.graphics.createBitmap

actual fun ByteArray.toImageBitmapOrNull(maxDimension: Int): ImageBitmap? {
    if (maxDimension <= 0) {
        return runCatching {
            BitmapFactory.decodeByteArray(this, 0, size)?.asImageBitmap()
        }.getOrNull()
    }
    val bytes = this
    return runCatching {
        val src = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return@runCatching null
        val scale = minOf(
            maxDimension.toFloat() / src.width,
            maxDimension.toFloat() / src.height,
            1f,
        )
        val result = if (scale >= 1f) {
            src
        } else {
            val dstW = maxOf(1, (src.width * scale).toInt())
            val dstH = maxOf(1, (src.height * scale).toInt())
            val srcPixels = IntArray(src.width * src.height)
            src.getPixels(srcPixels, 0, src.width, 0, 0, src.width, src.height)
            src.recycle()
            val dstPixels = LanczosDownscaler.downscale(src.width, src.height, srcPixels, dstW, dstH)
            val dst = createBitmap(dstW, dstH)
            dst.setPixels(dstPixels, 0, dstW, 0, 0, dstW, dstH)
            dst
        }
        result.asImageBitmap()
    }.getOrNull()
}

actual fun ByteArray.generateThumbnail(maxDimension: Int): ByteArray {
    val bytes = this
    val src = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        ?: throw IllegalStateException("Failed to decode image for thumbnail")
    val scale = minOf(
        maxDimension.toFloat() / src.width,
        maxDimension.toFloat() / src.height,
        1f,
    )
    val scaled = if (scale >= 1f) {
        src
    } else {
        val dstW = maxOf(1, (src.width * scale).toInt())
        val dstH = maxOf(1, (src.height * scale).toInt())
        val srcPixels = IntArray(src.width * src.height)
        src.getPixels(srcPixels, 0, src.width, 0, 0, src.width, src.height)
        src.recycle()
        val dstPixels = LanczosDownscaler.downscale(src.width, src.height, srcPixels, dstW, dstH)
        val dst = createBitmap(dstW, dstH)
        dst.setPixels(dstPixels, 0, dstW, 0, 0, dstW, dstH)
        dst
    }
    return ByteArrayOutputStream().use { stream ->
        scaled.compress(Bitmap.CompressFormat.JPEG, 85, stream)
        stream.toByteArray()
    }
}
