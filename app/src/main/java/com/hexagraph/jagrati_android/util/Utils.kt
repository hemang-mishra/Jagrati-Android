package com.hexagraph.jagrati_android.util

import android.content.Context
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import com.hexagraph.jagrati_android.model.ResponseError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import java.io.FileNotFoundException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.min
import kotlin.math.roundToInt
import androidx.core.graphics.scale
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.toBitmap
import com.hexagraph.jagrati_android.BuildConfig
import kotlin.math.max
import kotlin.math.sqrt

/**
 * A set of fairly general Android utility methods.
 */
object Utils {
    /**
     * A safe API call wrapper that handles exceptions and converts them to [Resource] responses
     * with appropriate error handling. Works with both Retrofit and Ktor implementations.
     *
     * @param timeout Optional timeout in milliseconds
     * @param apiCall The suspending API call to execute safely
     * @return A [Resource] wrapping the API result or error
     */
    suspend inline fun <reified T> safeApiCall(
        timeout: Long? = null,
        crossinline apiCall: suspend () -> T
    ): Resource<T> {
        return try {
            // Apply timeout if specified
            val result = if (timeout != null) {
                withTimeout(timeout) {
                    apiCall()
                }
            } else {
                apiCall()
            }
            Resource.success(result)
        } catch (e: CancellationException) {
            // Don't catch cancellation exceptions - let them propagate
            throw e
        } catch (e: TimeoutCancellationException) {
            logException(e)
            Resource.failure(error = ResponseError.SERVICE_UNAVAILABLE.apply {
                actualResponse = "Request timed out after ${timeout}ms"
            })
        } catch (e: HttpRequestTimeoutException) {
            logException(e)
            Resource.failure(error = ResponseError.SERVICE_UNAVAILABLE.apply {
                actualResponse = "Request timed out"
            })
        } catch (e: ClientRequestException) {
            // Handle 4xx responses from Ktor
            logException(e)
            handleKtorResponseException(e)
        } catch (e: RedirectResponseException) {
            // Handle 3xx responses from Ktor
            logException(e)
            Resource.failure(error = ResponseError.BAD_REQUEST.apply {
                actualResponse = "Redirect error"
            })
        } catch (e: ServerResponseException) {
            // Handle 5xx responses from Ktor
            logException(e)
            when (e.response.status.value) {
                500 -> ResponseError.INTERNAL_SERVER_ERROR
                502 -> ResponseError.BAD_GATEWAY
                503 -> ResponseError.SERVICE_UNAVAILABLE
                else -> ResponseError.UNKNOWN
            }.apply { actualResponse = e.message }.let { error ->
                Resource.failure(error = error)
            }
        } catch (e: IllegalArgumentException) {
            Log.e("SafeApiCall-caught", "IllegalArgumentException")
            logException(e)
            Resource.failure(error = ResponseError.BAD_REQUEST.apply { actualResponse = e.message })
        } catch (e: SocketTimeoutException) {
            logException(e)
            Resource.failure(error = ResponseError.SERVICE_UNAVAILABLE.apply {
                actualResponse = "Connection timed out"
            })
        } catch (e: UnknownHostException) {
            logException(e)
            Resource.failure(error = ResponseError.NETWORK_ERROR.apply {
                actualResponse = "Unable to resolve host"
            })
        } catch (e: ConnectException) {
            logException(e)
            Resource.failure(error = ResponseError.NETWORK_ERROR.apply {
                actualResponse = "Failed to connect to server"
            })
        } catch (e: ResponseException) {
            Log.e("SafeApiCall-caught", "ResponseException")
            // Handle Retrofit HTTP exceptions
            logException(e)
            when (e.response.status.value) {
                400 -> ResponseError.BAD_REQUEST
                401 -> ResponseError.AUTH_HEADER_NOT_FOUND
                403 -> ResponseError.UNAUTHORISED
                404 -> ResponseError.DOES_NOT_EXIST
                429 -> ResponseError.RATE_LIMIT_EXCEEDED
                500 -> ResponseError.INTERNAL_SERVER_ERROR
                502 -> ResponseError.BAD_GATEWAY
                503 -> ResponseError.SERVICE_UNAVAILABLE
                else -> ResponseError.UNKNOWN
            }.apply { actualResponse = e.message }.let { error ->
                Resource.failure(error = error)
            }
        } catch (e: IOException) {
            logException(e)
            when (e) {
                is FileNotFoundException -> Resource.failure(error = ResponseError.FILE_NOT_FOUND.apply {
                    actualResponse = e.message
                })

                else -> Resource.failure(error = ResponseError.NETWORK_ERROR.apply {
                    actualResponse = e.message
                })
            }
        } catch (e: NoTransformationFoundException) {
            Log.e("SafeApiCall-caught", "NoTransformationFoundException")
            logException(e)

            Resource.failure(error = ResponseError.INTERNAL_SERVER_ERROR.apply {
                actualResponse = "Some error occurred. Error parsing server response."
            })
        } catch (e: JsonConvertException) {
            Log.e("SafeApiCall-caught", "JsonConvertException ${e.cause}")
            logException(e)

            Resource.failure(error = ResponseError.INTERNAL_SERVER_ERROR.apply {
                actualResponse = "Some error occurred. Error parsing server response."
            })
        } catch (e: Exception) {
            Log.e("SafeApiCall-caught", "Exception")
            logException(e)
            Resource.failure(error = ResponseError.UNKNOWN.apply { actualResponse = e.message })
        }
    }

    /**
     * Handle Ktor response exceptions by mapping to appropriate ResponseError types
     */
    suspend fun <T> handleKtorResponseException(e: ResponseException): Resource<T> {
        val errorBody = try {
            e.response.bodyAsText()
        } catch (_: Exception) {
            null
        }
        Log.e("SafeApiCall-Handle", "Error body: $errorBody")
        val errorResponse = parseErrorResponse(errorBody)
        Log.e("SafeApiCall-Handle", "Error response: $errorResponse")
//        val errorMessage = errorResponse?.message ?: "Unknown error"

        val error = when (e.response.status.value) {
            400 -> ResponseError.BAD_REQUEST
            401 -> ResponseError.AUTH_HEADER_NOT_FOUND
            403 -> ResponseError.UNAUTHORISED
            404 -> ResponseError.DOES_NOT_EXIST
            429 -> ResponseError.RATE_LIMIT_EXCEEDED
            else -> ResponseError.UNKNOWN
        }.apply {
            if(errorResponse?.message != null)
                actualResponse = errorResponse.message
            else
                actualResponse = when(e.response.status.value) {
                    400 -> "Bad Request"
                    401 -> "Authentication header not found"
                    403 -> "Unauthorised"
                    404 -> "Resource does not exist"
                    429 -> "Rate limit exceeded"
                    else -> "Unknown error"
                }

        }

        return Resource.failure(error = error)
    }


    fun parseErrorResponse(errorBody: String?): ErrorResponse? {
        return try {
            if (errorBody.isNullOrBlank()) return null
            Json { ignoreUnknownKeys = true }.decodeFromString<ErrorResponse>(errorBody)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Log exception details and stack trace
     */
    fun logException(e: Exception) {
        e.printStackTrace()
        Log.e(
            "SafeApiCall",
            "API call failed with exception: ${e.javaClass.simpleName}, message: ${e.message}"
        )
    }


    fun timestamp(pattern: String = "yyyy-MM-dd HH:mm:ss", date: Date = Date()): String =
        SimpleDateFormat(pattern, Locale.getDefault()).format(date)

    fun PIDGenerator(timeInMills: Long = System.currentTimeMillis(), name: String): String {
        val sanitizedName = name.replace(Regex("[^a-zA-Z0-9]"), "_")
        return "${sanitizedName}_$timeInMills"
    }

    fun bitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File {
        val file = File(context.cacheDir, fileName)
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        return file
    }

    suspend fun getBitmapFromURL(context: Context, url: String): Bitmap? {
        try {
            Log.d("Utils", "Attempting to load image from: $url")
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
                Log.d("Utils", "Image loaded successfully from: $url")
                result.image.toBitmap()
            } else {
                Log.e("Utils", "Failed to load image: $result")
                null
            }
        } catch (e: Exception) {
            Log.e("Utils", "Exception loading image: ${e.javaClass.name}: ${e.message}")
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
                    Log.d("BitmapCompress", "Compressed to ${compressed.size} bytes (target: $targetBytes)")
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
            working = Bitmap.createScaledBitmap(working, newW, newH, true)
        }

        // Fallback: write best bytes found (may be > target)
        return@withContext try {
            val finalBytes = bestBytes ?: compressToQuality(working, minQuality)
            val file = File(context.cacheDir, fileName)
            file.outputStream().use { out -> out.write(finalBytes) }
            Log.w("BitmapCompress", "Final size ${finalBytes.size} bytes exceeds target $targetBytes")
            file
        } catch (e: Exception) {
            Log.e("BitmapCompress", "Final compression failed: ${e.message}")
            null
        }
    }

    //Allowing debug versions to be treated as college email id
    fun isCollegeEmailId(email: String): Boolean{
        return email.endsWith("@iiitdmj.ac.in") || BuildConfig.DEBUG
    }
}

@Serializable
data class ErrorResponse(
    val timestamp: String,
    val status: Int,
    val error: String,
    val message: String,
    val path: String?
)