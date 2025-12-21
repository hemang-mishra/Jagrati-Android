package com.hexagraph.jagrati_android.util

import android.content.Context
import android.graphics.Bitmap
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sqrt
import androidx.core.graphics.scale
import androidx.core.graphics.get
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.InterpreterApi
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

object AIIntegration {
    private const val FACE_NET_MODEL_PATH = "face_net_512.tflite"
    private const val MOBILE_NET_MODEL_PATH = "mobile_net.tflite"

    const val FACE_NET_IMAGE_SIZE = 160
    const val FACE_NET_EMBEDDING_SIZE = 512
    const val MOBILE_NET_IMAGE_SIZE = 224

    private const val IMAGE_MEAN = 128.0f
    private const val IMAGE_STD = 128.0f
    const val DEFAULT_SIMILARITY = 0.2f
    var isRunning = false

    private fun getInterceptor(path: String, context: Context): Interpreter {

        val fileDescriptor = context.assets.openFd(path)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val modelBuffer: MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        return Interpreter(modelBuffer)
    }
    val Context.faceNetInterceptor get() = getInterceptor(FACE_NET_MODEL_PATH, this)
    val Context.mobileNetInterceptor get() = getInterceptor(MOBILE_NET_MODEL_PATH, this)


    //Prepare the input Bitmap for MobileFaceNet
    fun preprocessBitmapForMobileFaceNet(bitmap: Bitmap, size: Int, isModelQuantized: Boolean = false): Result<ByteBuffer> = runCatching {
        val resizedBitmap = bitmap.scale(size, size)
        val inputBuffer = ByteBuffer.allocateDirect(size*size*3*4).apply {
            order(ByteOrder.nativeOrder())
        }
        for( y in 0 until size){
            for (z in 0 until size){
                val pixelValue = resizedBitmap[z, y]
                if (isModelQuantized) {
                    // Quantized model
                    inputBuffer.put((pixelValue shr 16 and 0xFF).toByte())
                    inputBuffer.put((pixelValue shr 8 and 0xFF).toByte())
                    inputBuffer.put((pixelValue and 0xFF).toByte())
                } else {
                    // Float model
                    inputBuffer.putFloat(((pixelValue shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    inputBuffer.putFloat(((pixelValue shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    inputBuffer.putFloat(((pixelValue and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                }
            }
        }
        inputBuffer
    }.onFailure {
        CrashlyticsHelper.logError("AIIntegration", it.message?:"Error while preprocessing bitmap for MobileFaceNet")
    }

    // Calculate the cosine similarity between two embeddings
    fun calculateCosineSimilarity(embeddingBuffer1: ByteBuffer, embeddingBuffer2: ByteBuffer): Result<Float> = runCatching {
        var dotProduct = 0.0f
        var norm1 =0.0f
        var norm2 = 0.0f

        for (i in 0 until FACE_NET_EMBEDDING_SIZE){
            val value1 = embeddingBuffer1.getFloat(i*4)
            val value2 = embeddingBuffer2.getFloat(i*4)
            dotProduct += value1*value2
            norm1 += value1*value1
            norm2 += value2*value2
        }

        norm1 = sqrt(norm1)
        norm2 = sqrt(norm2)

        dotProduct / (norm2 * norm1)
    }.onFailure {
        CrashlyticsHelper.logError("AIIntegration", it.message?:"Error while calculating cosine similarity")
    }

    fun calculateDistanceBtwEmbeddings(embeddingBuffer1: ByteBuffer, embeddingBuffer2: ByteBuffer): Result<Float> = runCatching {
        var sum = 0.0f
        for(i in 0 until FACE_NET_EMBEDDING_SIZE){
            val diff = embeddingBuffer1.getFloat(i*4) - embeddingBuffer2.getFloat(i*4)
            sum +=diff*diff
        }
        sqrt(sum.toDouble()).toFloat()
    }.onFailure {
        CrashlyticsHelper.logError("AIIntegration", it.message?:"Error while calculating distance between embeddings")
    }
}