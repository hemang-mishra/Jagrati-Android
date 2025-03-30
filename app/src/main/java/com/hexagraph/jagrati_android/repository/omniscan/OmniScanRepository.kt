package com.hexagraph.jagrati_android.repository.omniscan

import android.graphics.Paint
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.common.util.concurrent.ListenableFuture
import com.hexagraph.jagrati_android.model.FaceInfo
import com.hexagraph.jagrati_android.model.ProcessedImage
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executor

interface OmniScanRepository {
    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    fun cameraSelector(lensFacing: Int): CameraSelector

    val faces: Flow<List<FaceInfo>>

    suspend fun saveFace(image: ProcessedImage): Result<Unit>

    fun imageAnalyzer(lensFacing: Int, paint: Paint, cameraExecutor: Executor, onFaceInfo: (Result<ProcessedImage>) -> Unit): ImageAnalysis.Analyzer
}