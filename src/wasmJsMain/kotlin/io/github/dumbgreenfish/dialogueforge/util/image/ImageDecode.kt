package io.github.dumbgreenfish.dialogueforge.util.image

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo

actual fun ByteArray.toImageBitmapOrNull(maxDimension: Int): ImageBitmap? = runCatching {
    val image = Image.makeFromEncoded(this)
    if (maxDimension <= 0 || maxOf(image.width, image.height) <= maxDimension) {
        image.toComposeImageBitmap()
    } else {
        val ratio = minOf(
            maxDimension.toFloat() / image.width,
            maxDimension.toFloat() / image.height,
        )
        val newW = maxOf(1, (image.width * ratio).toInt())
        val newH = maxOf(1, (image.height * ratio).toInt())

        val srcBitmap = Bitmap.makeFromImage(image)
        val srcBytes = srcBitmap.readPixels() ?: return@runCatching null
        val srcPixels = bgraToIntArray(srcBytes, image.width, image.height)
        val dstPixels = LanczosDownscaler.downscale(image.width, image.height, srcPixels, newW, newH)
        val dstBytes = intArrayToBgra(dstPixels, newW, newH)

        Image.makeRaster(ImageInfo.makeN32Premul(newW, newH), dstBytes, newW * 4)
            .toComposeImageBitmap()
    }
}.getOrNull()

actual fun ByteArray.generateThumbnail(maxDimension: Int): ByteArray {
    val image = Image.makeFromEncoded(this)
    val ratio = minOf(1f, maxDimension.toFloat() / image.width, maxDimension.toFloat() / image.height)
    val newW = maxOf(1, (image.width * ratio).toInt())
    val newH = maxOf(1, (image.height * ratio).toInt())

    val resultImage = if (newW == image.width && newH == image.height) {
        image
    } else {
        val srcBitmap = Bitmap.makeFromImage(image)
        val srcBytes = srcBitmap.readPixels() ?: return this@generateThumbnail
        val srcPixels = bgraToIntArray(srcBytes, image.width, image.height)
        val dstPixels = LanczosDownscaler.downscale(image.width, image.height, srcPixels, newW, newH)
        val dstBytes = intArrayToBgra(dstPixels, newW, newH)
        Image.makeRaster(ImageInfo.makeN32Premul(newW, newH), dstBytes, newW * 4)
    }

    return resultImage.encodeToData(EncodedImageFormat.JPEG, 85)?.bytes ?: this@generateThumbnail
}

private fun bgraToIntArray(bytes: ByteArray, width: Int, height: Int): IntArray {
    val pixels = IntArray(width * height)
    for (i in pixels.indices) {
        val offset = i * 4
        val b = bytes[offset].toInt() and 0xFF
        val g = bytes[offset + 1].toInt() and 0xFF
        val r = bytes[offset + 2].toInt() and 0xFF
        val a = bytes[offset + 3].toInt() and 0xFF
        pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
    }
    return pixels
}

private fun intArrayToBgra(pixels: IntArray, width: Int, height: Int): ByteArray {
    val bytes = ByteArray(width * height * 4)
    for (i in pixels.indices) {
        val p = pixels[i]
        val offset = i * 4
        bytes[offset] = (p and 0xFF).toByte()
        bytes[offset + 1] = (p shr 8 and 0xFF).toByte()
        bytes[offset + 2] = (p shr 16 and 0xFF).toByte()
        bytes[offset + 3] = (p shr 24 and 0xFF).toByte()
    }
    return bytes
}
