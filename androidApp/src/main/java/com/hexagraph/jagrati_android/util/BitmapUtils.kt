package com.hexagraph.jagrati_android.util

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.scale
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import com.hexagraph.jagrati_android.model.Student
import com.hexagraph.jagrati_android.util.FileUtility.readBitmapFromFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt


fun Student.faceBitmap(context: Context): Bitmap? = context.readBitmapFromFile(faceFileName).getOrNull()
fun Student.imageBitmap(context: Context): Bitmap? = context.readBitmapFromFile(imageFileName).getOrNull()
fun Student.frameBitmap(context: Context): Bitmap? = context.readBitmapFromFile(frameFileName).getOrNull()

fun bitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File {
    val file = File(context.cacheDir, fileName)
    file.outputStream().use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
    return file
}

suspend fun getBitmapFromURL(context: Context, url: String): Bitmap? {
    try {
        LogHelper.log("Utils", "Attempting to load image from: $url")
        val loader = ImageLoader.Builder(context).components {
            add(OkHttpNetworkFetcherFactory())
        }
            .build()
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .build()


        val result = loader.execute(request)
        return if (result is SuccessResult) {
            LogHelper.log("Utils", "Image loaded successfully from: $url")
            result.image.toBitmap()
        } else {
            LogHelper.log("Utils", "Failed to load image: $result")
            null
        }
    } catch (e: Exception) {
        LogHelper.log("Utils", "Exception loading image: ${e.javaClass.name}: ${e.message}")
        e.printStackTrace()
        return null
    }
}

suspend fun compressBitmapToFile(
    context: Context,
    bitmap: Bitmap,
    fileName: String,
    targetBytes: Long,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    maxHeight: Int = 1280,
    maxWidth: Int = 1280,
    minQuality: Int = 25,
    initialQuality: Int = 95,
    stepSize: Int = 7,
    minSidePx: Int = 160,
    noOfIterations: Int = 15
): File? = withContext(Dispatchers.IO) {
    if (targetBytes <= 0) return@withContext null

    fun scaleMaintainingAspectRatio(bmp: Bitmap, targetH: Int, targetW: Int): Bitmap {
        val h = bmp.height
        val w = bmp.width
        if (h <= targetH && w <= targetW) return bmp
        val ratio = min(targetH.toFloat() / h, targetW.toFloat() / w)
        val newH = (h * ratio).roundToInt()
        val newW = (w * ratio).roundToInt()
        return bmp.scale(newW, newH)
    }

    fun compressToQuality(bmp: Bitmap, q: Int): ByteArray {
        val baos = ByteArrayOutputStream()
        baos.use {
            bmp.compress(format, q.coerceIn(0, 100), it)
            it.flush()
            return it.toByteArray()
        }
    }

    var iterations = 0
    var working = scaleMaintainingAspectRatio(bitmap, maxHeight, maxWidth)
    var bestBytes: ByteArray? = null
    var bestSize: Long = Long.MAX_VALUE

    outer@ while (iterations++ < noOfIterations) {
        var quality = initialQuality
        while (quality >= minQuality) {
            val compressed = try {
                compressToQuality(working, quality)
            } catch (_: Throwable) {
                break
            }
            val size = compressed.size.toLong()
            if (size < bestSize) {
                bestSize = size
                bestBytes = compressed
            }
            if (size <= targetBytes) {
                // Found acceptable size, write to file and return
                val file = File(context.cacheDir, fileName)
                file.outputStream().use { out -> out.write(compressed) }
                LogHelper.log("BitmapCompress", "Compressed to ${compressed.size} bytes (target: $targetBytes)")
                return@withContext file
            }
            quality -= stepSize
        }

        val currentMinSide = min(working.width, working.height)
        if (currentMinSide <= minSidePx) break@outer

        val currentEstimatedSize = bestSize.takeIf { it != Long.MAX_VALUE } ?: try {
            compressToQuality(working, minQuality).size.toLong()
        } catch (_: Throwable) {
            Long.MAX_VALUE
        }

        if (currentEstimatedSize == Long.MAX_VALUE) break@outer

        val scaleRatio =
            sqrt(targetBytes.toDouble() / currentEstimatedSize.toDouble()).coerceAtMost(0.95)
        if (scaleRatio <= 0.01) break@outer

        val newW = max((working.width * scaleRatio).roundToInt(), minSidePx)
        val newH = max((working.height * scaleRatio).roundToInt(), minSidePx)
        working = working.scale(newW, newH)
    }

    // Fallback: write best bytes found (may be > target)
    return@withContext try {
        val finalBytes = bestBytes ?: compressToQuality(working, minQuality)
        val file = File(context.cacheDir, fileName)
        file.outputStream().use { out -> out.write(finalBytes) }
        LogHelper.log("BitmapCompress", "Final size ${finalBytes.size} bytes exceeds target $targetBytes")
        file
    } catch (e: Exception) {
        LogHelper.log("BitmapCompress", "Final compression failed: ${e.message}")
        null
    }
}