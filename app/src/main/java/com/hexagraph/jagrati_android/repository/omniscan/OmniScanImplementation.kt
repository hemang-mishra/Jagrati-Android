package com.hexagraph.jagrati_android.repository.omniscan

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark
import com.hexagraph.jagrati_android.model.ProcessedImage
import com.hexagraph.jagrati_android.util.MediaUtils.crop
import com.hexagraph.jagrati_android.util.MediaUtils.flip
import kotlin.math.atan2
import androidx.core.graphics.createBitmap
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.hexagraph.jagrati_android.model.FaceInfo
import com.hexagraph.jagrati_android.model.databases.PrimaryDatabase
import com.hexagraph.jagrati_android.service.FaceRecognitionService
import com.hexagraph.jagrati_android.util.FileUtility.writeBitmapIntoFile
import com.hexagraph.jagrati_android.util.MediaUtils.bitmap
import com.hexagraph.jagrati_android.util.Utils
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executor
import javax.inject.Inject

class OmniScanImplementation @Inject constructor(
    private val faceRecognitionService: FaceRecognitionService,
    private val application: Application,
    private val db: PrimaryDatabase
): OmniScanRepository {
    override fun cameraSelector(lensFacing: Int): CameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
//    val cameraExecutor: Executor by lazy { Executors.newSingleThreadExecutor() }
    override val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> by lazy { ProcessCameraProvider.getInstance(application) }
    val faceDetector: FaceDetector by lazy {
        val options = FaceDetectorOptions.Builder()
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        FaceDetection.getClient(options)
    }

    // Functions to handle local database operations
    override val faces: Flow<List<FaceInfo>> = db.faceInfoDao().faces()
    suspend fun faceList(): List<FaceInfo> = db.faceInfoDao().faceList()

    override suspend fun saveFace(image: ProcessedImage) = runCatching {
        val info = image.faceInfo
        val images = faceList().map { it.processedImage(application) }
        if (image.faceBitmap == null) throw Throwable("Face is empty")
        if (images.find { image.name == it.name } != null) throw Throwable("Name Already Exist.")
        if ((faceRecognitionService.recognizeFace(image, images, application)?.matchesCriteria == true)) throw Throwable("Face Already Exist.")
        image.faceBitmap.let { application.writeBitmapIntoFile(info.faceFileName, it).getOrNull() }
        image.frame?.let { application.writeBitmapIntoFile(info.frameFileName, it).getOrNull() }
        image.image?.let { application.writeBitmapIntoFile(info.imageFileName, it).getOrNull() }
        db.faceInfoDao().insert(info)
    }.onFailure {
        Log.e("MediaUtils", it.message ?: "Error while saving face")
    }

    suspend fun deleteFace(face: FaceInfo) = runCatching {
        if (face.pid == null) throw Throwable("Invalid Face Id")
        db.faceInfoDao().delete(face.pid)
        application.deleteFile(face.faceFileName)
        application.deleteFile(face.frameFileName)
        application.deleteFile(face.imageFileName)
    }.onFailure {
        Log.e("MediaUtils", it.message ?: "Error while deleting face")
    }

//    fun imageAnalysis(lensFacing: Int, paint: Paint, onData: (Result<ProcessedImage>) -> Unit): ImageAnalysis {
//        val imageAnalyzer: ImageAnalysis.Analyzer = imageAnalyzer(lensFacing, paint, onData)
//        val imageAnalysis = ImageAnalysis.Builder().apply {
//            setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//            setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
//        }.build()
//        imageAnalysis.setAnalyzer(cameraExecutor, imageAnalyzer)
//        return imageAnalysis
//    }

    @OptIn(ExperimentalGetImage::class)
    override fun imageAnalyzer(lensFacing: Int, paint: Paint, cameraExecutor: Executor, onFaceInfo: (Result<ProcessedImage>) -> Unit): ImageAnalysis.Analyzer =
        ImageAnalysis.Analyzer { imageProxy ->
            runCatching {
                val mediaImage = imageProxy.image ?: throw Throwable("Unable to get Media Image")
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                val bitmap = imageProxy.bitmap.getOrNull() ?: throw Throwable("Unable to get Bitmap")
                faceDetector.process(image)
                    .addOnSuccessListener(cameraExecutor) { onFaceInfo(processImage(lensFacing, it, bitmap, paint)) }
                    .addOnFailureListener(cameraExecutor) {
                        Log.e("MediaUtils", it.message ?: "Error while processing image")
                    }
                    .addOnCompleteListener { imageProxy.close() }
            }.onFailure {
                Log.e("MediaUtils", it.message ?: "Error while processing image")
            }
        }

    fun processImage(
        lensFacing: Int,
        data: MutableList<Face>,
        bitmap: Bitmap,
        paint: Paint
    ): Result<ProcessedImage> = runCatching {
        paint.style = Paint.Style.STROKE
        val face = biggestFace(data)
        var frame = bitmap.config?.let { createBitmap(bitmap.width, bitmap.height, it) }
        var faceBitmap = face?.boundingBox?.let { bitmap.crop(it.left, it.top, it.width(), it.height()).getOrNull() }
        val canvas = frame?.let { Canvas(it) }
//        canvas?.drawBitmap(bitmap, 0f, 0f, null)
        data.forEach { canvas?.drawRect(it.boundingBox, paint) }
        face?.allLandmarks?.forEach { canvas?.drawPoint(it.position.x, it.position.y, paint) }
        if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
            frame = frame?.flip(horizontal = true)?.getOrNull()
            faceBitmap = faceBitmap?.flip(horizontal = true)?.getOrNull()
        }
        faceBitmap = faceBitmap?.let { alignBitmapByLandmarks(bitmap = it, face?.allLandmarks ?: listOf()).getOrNull() }
        return@runCatching ProcessedImage(image = bitmap, frame = frame, face = face, trackingId = face?.trackingId, faceBitmap = faceBitmap)
    }.onFailure {
        Log.e("MediaUtils", it.message ?: "Error while processing image")
    }

    fun biggestFace(faces: MutableList<Face>): Face? {
        var biggestFace: Face? = null
        var biggestFaceSize = 0
        for (face in faces) {
            val faceSize = face.boundingBox.height() * face.boundingBox.width()
            if (faceSize > biggestFaceSize) {
                biggestFaceSize = faceSize
                biggestFace = face
            }
        }
        return biggestFace
    }

    // Function to align a bitmap based on facial landmarks
    fun alignBitmapByLandmarks(bitmap: Bitmap, landmarks: List<FaceLandmark>, noseRatio: Float = 0.4f, eyeDistanceRatio: Float = 0.3f): Result<Bitmap> = runCatching {
        val leftEye = landmarks.find { it.landmarkType == FaceLandmark.LEFT_EYE }?.position
        val rightEye = landmarks.find { it.landmarkType == FaceLandmark.RIGHT_EYE }?.position
        val noseBase = landmarks.find { it.landmarkType == FaceLandmark.NOSE_BASE }?.position

        if (leftEye == null || rightEye == null || noseBase == null) return@runCatching bitmap

        val matrix = Matrix()

        val eyeCenterX = (leftEye.x + rightEye.x) / 2f
        val eyeCenterY = (leftEye.y + rightEye.y) / 2f
        val dx = rightEye.x - leftEye.x
        val dy = rightEye.y - leftEye.y
        val angle = atan2(dy.toDouble(), dx.toDouble()) * 180 / Math.PI

        matrix.postTranslate(-eyeCenterX, -eyeCenterY)
        matrix.postRotate(angle.toFloat(), 0f, 0f)

        // Calculate the desired eye distance based on a fixed ratio
        val desiredEyeDistance = bitmap.width * eyeDistanceRatio

        val scale = desiredEyeDistance / dx
        matrix.postScale(scale, scale)

        // Calculate the translation to bring the nose base to a fixed position
        val targetNoseY = bitmap.height * noseRatio
        val translationY = targetNoseY - noseBase.y * scale
        matrix.postTranslate(0f, translationY)

        // Apply the transformation matrix to the bitmap
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }.onFailure {
        Log.e("MediaUtils", it.message ?: "Error while aligning bitmap by landmarks")
    }

}