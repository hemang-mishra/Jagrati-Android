package com.hexagraph.jagrati_android.service.face_recognition

import android.content.Context
import android.util.Log
import com.hexagraph.jagrati_android.model.FaceEmbeddingsCacheEntity
import com.hexagraph.jagrati_android.model.ProcessedImage
import com.hexagraph.jagrati_android.model.dao.EmbeddingsDAO
import com.hexagraph.jagrati_android.model.dao.FaceInfoDao
import com.hexagraph.jagrati_android.util.AIIntegration
import com.hexagraph.jagrati_android.util.AIIntegration.DEFAULT_SIMILARITY
import com.hexagraph.jagrati_android.util.AIIntegration.FACE_NET_EMBEDDING_SIZE
import com.hexagraph.jagrati_android.util.AIIntegration.FACE_NET_IMAGE_SIZE
import com.hexagraph.jagrati_android.util.AIIntegration.calculateCosineSimilarity
import com.hexagraph.jagrati_android.util.AIIntegration.faceNetInterceptor
import com.hexagraph.jagrati_android.util.AIIntegration.isRunning
import com.hexagraph.jagrati_android.util.AIIntegration.mobileNetInterceptor
import com.hexagraph.jagrati_android.util.AIIntegration.preprocessBitmapForMobileFaceNet
import kotlinx.coroutines.runBlocking
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Collections
import java.util.PriorityQueue
import javax.inject.Inject

data class FaceRecognitionResult(
    val pid: String,
    val distance: Float,
    val similarity: Float
){
    val matchesCriteria get():Boolean = (similarity) > DEFAULT_SIMILARITY
}

class FaceRecognitionServiceImpl @Inject constructor(val faceEmbeddingsDAO: EmbeddingsDAO,
    val faceInfoDao: FaceInfoDao):
    FaceRecognitionService {
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
        Log.e(
            "FaceRecognitionServiceImpl",
            it.message ?: "Error while preprocessing bitmap for MobileFaceNet"
        )
    }


    override fun recognizeFace(
        face: ProcessedImage,
        facePids: List<String>,
        context: Context
    ): List<FaceRecognitionResult>? {
        synchronized(this) {
            if (AIIntegration.isRunning) return@synchronized
            isRunning = true
        }
        return try {
            runBlocking {
                val start = System.currentTimeMillis()
                var embeddingTime = 0L

                // Reference face preprocessing and embedding
                val referenceInput =
                    face.faceBitmap?.let { bitmap ->
                        preprocessBitmapForMobileFaceNet(
                            bitmap,
                            FACE_NET_IMAGE_SIZE
                        ).getOrNull()?.let { arrayOf(it) }
                    }
                        ?: throw Throwable("Unable to preprocess Bitmap")

                val referenceOutputBuffer =
                    ByteBuffer.allocateDirect(FACE_NET_EMBEDDING_SIZE * 4).apply {
                        order(ByteOrder.nativeOrder())
                    }

                val referenceOutputs: MutableMap<Int, Any> =
                    mutableMapOf(0 to referenceOutputBuffer)
                context.faceNetInterceptor.runForMultipleInputsOutputs(
                    referenceInput,
                    referenceOutputs
                )
                val singleStart = System.currentTimeMillis()

                val thresholdDistance = Float.MAX_VALUE
                val minHeap = PriorityQueue<Pair<Float, FaceRecognitionResult>>(compareByDescending { it.first })

                for (testPid in facePids) {
                    val testEmbedding: FloatArray =
                        faceEmbeddingsDAO.getEmbeddingsByPid(testPid)?.embedding
                            ?: run {
                                val data = faceInfoDao.getFaceById(testPid)?.processedImage(context)
                                val testInputBuffer = data?.faceBitmap?.let {
                                    preprocessBitmapForMobileFaceNet(
                                        it,
                                        FACE_NET_IMAGE_SIZE
                                    ).getOrNull()
                                } ?: throw Throwable("Unable to preprocess Test Bitmap")

                                val testOutputBuffer =
                                    ByteBuffer.allocateDirect(FACE_NET_EMBEDDING_SIZE * 4).apply {
                                        order(ByteOrder.nativeOrder())
                                    }

                                val testInputs = arrayOf(testInputBuffer)
                                val testOutputs: MutableMap<Int, Any> =
                                    mutableMapOf(0 to testOutputBuffer)
                                val singleStart = System.currentTimeMillis()

                                context.faceNetInterceptor.runForMultipleInputsOutputs(
                                    testInputs,
                                    testOutputs
                                )

                                embeddingTime = System.currentTimeMillis()
                                Log.i(
                                    "FaceRecognitionServiceImpl",
                                    "Embedding time: ${embeddingTime - singleStart} ms"
                                )

                                // Convert buffer to float array
                                testOutputBuffer.rewind()
                                val embedding = FloatArray(FACE_NET_EMBEDDING_SIZE) {
                                    testOutputBuffer.float
                                }

                                // Cache embedding
                                faceEmbeddingsDAO.upsertEmbeddings(
                                    FaceEmbeddingsCacheEntity(
                                        pid = testPid,
                                        embedding = embedding
                                    )
                                )

                                embedding
                            }

                    // Rewind the reference buffer to read again
                    referenceOutputBuffer.rewind()

                    // Create a buffer from the cached/test embedding
                    val testBuffer = ByteBuffer.allocateDirect(FACE_NET_EMBEDDING_SIZE * 4).apply {
                        order(ByteOrder.nativeOrder())
                        asFloatBuffer().put(testEmbedding)
                        rewind()
                    }

                    // Calculate distance and similarity
                    val distance =
                        AIIntegration.calculateDistanceBtwEmbeddings(
                            referenceOutputBuffer,
                            testBuffer
                        )
                            .getOrNull()
                            ?: throw Throwable("Unable to calculate Distance")

                    val similarity =
                        calculateCosineSimilarity(referenceOutputBuffer, testBuffer).getOrNull()
                            ?: throw Throwable("Unable to calculate Cosine Similarity")

                    if (distance < thresholdDistance) {
                        if(minHeap.size >= 5){
                            minHeap.remove()
                        }
                        minHeap.offer(Pair(distance, FaceRecognitionResult(
                            pid = testPid,
                            distance = distance,
                            similarity = similarity
                        )))
                    }
                }

                val end = System.currentTimeMillis()
                Log.i("FaceRecognitionServiceImpl", "Pre test took ${singleStart - start}ms Face recognition took ${end - start} ms")
                context.faceNetInterceptor.close()
                minHeap.toList().map { it.second }
            }
        } catch (th: Throwable) {
            Log.e("FaceRecognitionServiceImpl", th.message ?: "Error while recognizing face")
            null
        } finally {
            synchronized(this) { isRunning = false }
        }
    }
}