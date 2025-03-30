package com.hexagraph.jagrati_android.service

import android.content.Context
import android.util.Log
import com.hexagraph.jagrati_android.model.ProcessedImage
import com.hexagraph.jagrati_android.util.AIIntegration
import com.hexagraph.jagrati_android.util.AIIntegration.FACE_NET_EMBEDDING_SIZE
import com.hexagraph.jagrati_android.util.AIIntegration.FACE_NET_IMAGE_SIZE
import com.hexagraph.jagrati_android.util.AIIntegration.calculateCosineSimilarity
import com.hexagraph.jagrati_android.util.AIIntegration.faceNetInterceptor
import com.hexagraph.jagrati_android.util.AIIntegration.isRunning
import com.hexagraph.jagrati_android.util.AIIntegration.mobileNetInterceptor
import com.hexagraph.jagrati_android.util.AIIntegration.preprocessBitmapForMobileFaceNet
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

class FaceRecognitionServiceImpl @Inject constructor(): FaceRecognitionService {
    override fun mobileNet(face: ProcessedImage, context: Context): Result<Float> = runCatching {
        val referenceInput = face.faceBitmap?.let { bitmap ->
            AIIntegration.preprocessBitmapForMobileFaceNet(
                bitmap,
                AIIntegration.MOBILE_NET_IMAGE_SIZE
            )
                .getOrNull().let {
                    arrayOf(it)
                }
        } ?: throw Throwable("Unable to preprocess bitmap for MobileFaceNet")
        val referencedOutputBuffer = ByteBuffer.allocateDirect(4).apply {
            order(ByteOrder.nativeOrder())
        }
        val referencedOutputs: MutableMap<Int, Any> = mutableMapOf(0 to referencedOutputBuffer)
        context.mobileNetInterceptor.run {
            runForMultipleInputsOutputs(referenceInput, referencedOutputs)
        }
        referencedOutputBuffer.rewind()
        val data = referencedOutputBuffer.float
        data
    }.onFailure {
        Log.e("FaceRecognitionServiceImpl", it.message ?: "Error while preprocessing bitmap for MobileFaceNet")
    }


    override fun recognizeFace(
        face: ProcessedImage,
        faces: List<ProcessedImage>,
        context: Context
    ): ProcessedImage? {
        synchronized(this) {
            if (AIIntegration.isRunning) return@synchronized
            isRunning = true
        }
        return try {
            // Preprocess the reference bitmap
            val referenceInput =
                face.faceBitmap?.let { bitmap -> preprocessBitmapForMobileFaceNet(bitmap, FACE_NET_IMAGE_SIZE).getOrNull()?.let { arrayOf(it) } }
                    ?: throw Throwable("Unable to preprocess Bitmap")
            // Allocate output buffer for the reference embedding
            val referenceOutputBuffer = ByteBuffer.allocateDirect(FACE_NET_EMBEDDING_SIZE * 4).apply { order(ByteOrder.nativeOrder()) }
            // Run inference for the reference bitmap
            val referenceOutputs: MutableMap<Int, Any> = mutableMapOf(0 to referenceOutputBuffer)
            context.faceNetInterceptor.runForMultipleInputsOutputs(referenceInput, referenceOutputs)
            // Process test bitmaps
            var image: ProcessedImage? = null
            var minDistance = Float.MAX_VALUE

            for (data in faces) {
                // Preprocess the test face
                val testInputBuffer =
                    data.faceBitmap?.let { preprocessBitmapForMobileFaceNet(it, FACE_NET_IMAGE_SIZE).getOrNull() } ?: throw Throwable("Unable to preprocess Test Bitmap")
                // Allocate output buffer for the test embedding
                val testOutputBuffer = ByteBuffer.allocateDirect(FACE_NET_EMBEDDING_SIZE * 4).apply { order(ByteOrder.nativeOrder()) }
                // Run inference for the test face
                val testInputs = arrayOf(testInputBuffer)
                val testOutputs: MutableMap<Int, Any> = mutableMapOf(0 to testOutputBuffer)
                context.faceNetInterceptor.runForMultipleInputsOutputs(testInputs, testOutputs)
                // Calculate the Euclidean distance between the reference and test embeddings
                val distance = AIIntegration.calculateDistanceBtwEmbeddings(referenceOutputBuffer, testOutputBuffer).getOrNull() ?: throw Throwable("Unable to calculate Distance")
                // Calculate the Cosine Similarity between the reference and test embeddings
                val similarity =
                    calculateCosineSimilarity(referenceOutputBuffer, testOutputBuffer).getOrNull() ?: throw Throwable("Unable to calculate Cosine Similarity")
                // Check if the distance is the smallest so far
                if (distance < minDistance) {
                    minDistance = distance
                    image = data.copy(distance = distance, similarity = similarity)
                }
            }
            // Cleanup
            context.faceNetInterceptor.close()
            image
        } catch (th: Throwable) {
            Log.e("FaceRecognitionServiceImpl", th.message ?: "Error while recognizing face")
            null
        } finally {
            synchronized(this) { isRunning = false }
        }
    }
}