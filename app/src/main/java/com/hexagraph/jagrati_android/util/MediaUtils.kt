package com.hexagraph.jagrati_android.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream

object MediaUtils {
    val ImageProxy.bitmap: Result<Bitmap?>
        get(): Result<Bitmap?> = runCatching {
            val yBuffer = planes[0].buffer
            val vuBuffer = planes[2].buffer

            val ySize = yBuffer.remaining()
            val vuSize = vuBuffer.remaining()

            val nv21 = ByteArray(ySize + vuSize)

            yBuffer.get(nv21, 0, ySize)
            vuBuffer.get(nv21, ySize, vuSize)

            val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
            val imageBytes = out.toByteArray()

            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            bitmap.rotate(imageInfo.rotationDegrees.toFloat()).getOrNull()
        }.onFailure {
            Log.e("MediaUtils", it.message ?: "Error while converting image proxy to bitmap")
        }

    fun Bitmap.rotate(rotation: Float): Result<Bitmap> = runCatching {
        val matrix = Matrix()
        matrix.postRotate(rotation)
        Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }.onFailure {
        Log.e("MediaUtils", it.message ?: "Error while rotating bitmap")
    }

    fun Bitmap.flip(horizontal: Boolean = false, vertical: Boolean = false): Result<Bitmap> =
        runCatching {
            val matrix = Matrix()
            if (horizontal)
                matrix.postScale(-1f, 1f)
            if (vertical)
                matrix.postScale(1f, -1f)
            Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        }.onFailure {
            Log.e("MediaUtils", it.message ?: "Error while flipping bitmap")
        }

    fun Bitmap.crop(left: Int, top: Int, width: Int, height: Int): Result<Bitmap> = runCatching {
        Bitmap.createBitmap(this, left, top, width, height)
    }.onFailure {
        Log.e("MediaUtils", it.message ?: "Error while cropping bitmap")
    }
}