package com.hexagraph.jagrati_android.model

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.mlkit.vision.face.FaceLandmark
import com.hexagraph.jagrati_android.util.FileUtility.readBitmapFromFile
import com.hexagraph.jagrati_android.util.Utils

@Keep
@Entity
data class FaceInfo(
    @PrimaryKey
    val pid: String = "",
    val name: String,
    val width: Int = 0,
    val height: Int = 0,
    val faceWidth: Int = 0,
    val faceHeight: Int = 0,
    val top: Int = 0,
    val left: Int = 0,
    val right: Int = 0,
    val bottom: Int = 0,
    val landmarks: List<FaceLandmark> = listOf(),
    val smilingProbability: Float = 0f,
    val leftEyeOpenProbability: Float = 0f,
    val rightEyeOpenProbability: Float = 0f,
    val timestamp: String = Utils.timestamp(),
    val time: Long = System.currentTimeMillis(),
) {
    val pattern get(): String = "${name}_${time}.png"
    val faceFileName get(): String = "Face_${pattern}"
    val imageFileName get(): String = "Image_${pattern}"
    val frameFileName get(): String = "Frame_${pattern}"
    fun faceBitmap(context: Context): Bitmap? = context.readBitmapFromFile(faceFileName).getOrNull()
    fun imageBitmap(context: Context): Bitmap? = context.readBitmapFromFile(imageFileName).getOrNull()
    fun frameBitmap(context: Context): Bitmap? = context.readBitmapFromFile(frameFileName).getOrNull()
    fun processedImage(context: Context): ProcessedImage {
        val image = imageBitmap(context)
        val frame = frameBitmap(context)
        val face = faceBitmap(context)
        return ProcessedImage(pid = pid, name = name, image = image, frame = frame, faceBitmap = face, landmarks = landmarks)
    }
}
