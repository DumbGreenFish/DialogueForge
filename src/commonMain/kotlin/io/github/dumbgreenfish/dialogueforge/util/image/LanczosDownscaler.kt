package io.github.dumbgreenfish.dialogueforge.util.image

import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.sin

internal object LanczosDownscaler {
    private const val A = 3

    fun downscale(
        srcWidth: Int,
        srcHeight: Int,
        srcPixels: IntArray,
        dstWidth: Int,
        dstHeight: Int,
    ): IntArray {
        val scaleX = srcWidth.toFloat() / dstWidth
        val scaleY = srcHeight.toFloat() / dstHeight
        val radiusX = ceil(A * scaleX).toInt()
        val radiusY = ceil(A * scaleY).toInt()

        val kernelsX = precomputeKernels(dstWidth, scaleX, radiusX)
        val kernelsY = precomputeKernels(dstHeight, scaleY, radiusY)

        val channelR = FloatArray(srcPixels.size)
        val channelG = FloatArray(srcPixels.size)
        val channelB = FloatArray(srcPixels.size)
        for (i in srcPixels.indices) {
            val p = srcPixels[i]
            channelR[i] = ((p shr 16) and 0xFF).toFloat()
            channelG[i] = ((p shr 8) and 0xFF).toFloat()
            channelB[i] = (p and 0xFF).toFloat()
        }

        val horR = FloatArray(dstWidth * srcHeight)
        val horG = FloatArray(dstWidth * srcHeight)
        val horB = FloatArray(dstWidth * srcHeight)

        for (y in 0 until srcHeight) {
            val srcRow = y * srcWidth
            val dstRow = y * dstWidth
            applyKernel(channelR, srcRow, horR, dstRow, srcWidth, dstWidth, scaleX, radiusX, kernelsX)
            applyKernel(channelG, srcRow, horG, dstRow, srcWidth, dstWidth, scaleX, radiusX, kernelsX)
            applyKernel(channelB, srcRow, horB, dstRow, srcWidth, dstWidth, scaleX, radiusX, kernelsX)
        }

        val colR = FloatArray(srcHeight)
        val colG = FloatArray(srcHeight)
        val colB = FloatArray(srcHeight)
        val result = IntArray(dstWidth * dstHeight)

        for (x in 0 until dstWidth) {
            for (y in 0 until srcHeight) {
                val idx = y * dstWidth + x
                colR[y] = horR[idx]
                colG[y] = horG[idx]
                colB[y] = horB[idx]
            }

            for (y in 0 until dstHeight) {
                val k = kernelsY[y]
                var sumR = 0f
                var sumG = 0f
                var sumB = 0f
                var weight = 0f

                for ((sampleY, w) in k) {
                    if (sampleY < 0 || sampleY >= srcHeight) continue
                    sumR += colR[sampleY] * w
                    sumG += colG[sampleY] * w
                    sumB += colB[sampleY] * w
                    weight += w
                }

                if (weight > 0f) {
                    val ir = (sumR / weight).toInt().coerceIn(0, 255)
                    val ig = (sumG / weight).toInt().coerceIn(0, 255)
                    val ib = (sumB / weight).toInt().coerceIn(0, 255)
                    result[y * dstWidth + x] = (0xFF shl 24) or (ir shl 16) or (ig shl 8) or ib
                }
            }
        }

        return result
    }

    private fun precomputeKernels(dstSize: Int, scale: Float, radius: Int): Array<List<Pair<Int, Float>>> {
        return Array(dstSize) { pos ->
            val srcCenter = pos * scale
            val result = mutableListOf<Pair<Int, Float>>()
            for (offset in -radius..radius) {
                val sample = srcCenter.toInt() + offset
                val w = lanczos3((srcCenter - sample) / scale)
                if (w != 0f) {
                    result.add(sample to w)
                }
            }
            result
        }
    }

    private fun applyKernel(
        src: FloatArray,
        srcOffset: Int,
        dst: FloatArray,
        dstOffset: Int,
        srcW: Int,
        dstW: Int,
        scale: Float,
        radius: Int,
        kernels: Array<List<Pair<Int, Float>>>,
    ) {
        for (x in 0 until dstW) {
            var sum = 0f
            var weight = 0f

            for ((sampleX, w) in kernels[x]) {
                if (sampleX < 0 || sampleX >= srcW) continue
                sum += src[srcOffset + sampleX] * w
                weight += w
            }

            dst[dstOffset + x] = if (weight > 0f) sum / weight else 0f
        }
    }

    private fun lanczos3(x: Float): Float {
        if (x <= -A || x >= A) return 0f
        if (x > -1e-5f && x < 1e-5f) return 1f
        val pix = PI.toFloat() * x
        val sinc = sin(pix) / pix
        val sincA = sin(pix / A) / (pix / A)
        return sinc * sincA
    }
}
