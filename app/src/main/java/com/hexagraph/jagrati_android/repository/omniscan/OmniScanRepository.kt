package com.hexagraph.jagrati_android.repository.omniscan

import android.graphics.Bitmap
import android.graphics.Paint
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.common.util.concurrent.ListenableFuture
import com.hexagraph.jagrati_android.model.ProcessedImage
import java.util.concurrent.Executor

interface OmniScanRepository {
    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    fun cameraSelector(lensFacing: Int): CameraSelector

    suspend fun saveFaceLocally(image: ProcessedImage, isStudent: Boolean): Result<Unit>
    suspend fun deleteFaceIfExists(pid: String): Result<Unit>

    fun imageAnalyzer(lensFacing: Int, paint: Paint, cameraExecutor: Executor, onFaceInfo: (Result<ProcessedImage>) -> Unit): ImageAnalysis.Analyzer

    suspend fun processImageFromBitmap(
        bitmap: Bitmap,
        paint: Paint
    ): Result<ProcessedImage>
}