package com.hexagraph.jagrati_android.util

import android.icu.text.SimpleDateFormat
import android.net.http.HttpException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
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
import kotlinx.serialization.SerializationException
import com.google.gson.JsonSyntaxException
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.JsonConvertException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

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
        } 
        catch (e: CancellationException) {
            // Don't catch cancellation exceptions - let them propagate
            throw e
        } catch (e: TimeoutCancellationException) {
            logException(e)
            Resource.failure(error = ResponseError.SERVICE_UNAVAILABLE.apply { actualResponse = "Request timed out after ${timeout}ms" })
        } catch (e: HttpRequestTimeoutException) {
            logException(e)
            Resource.failure(error = ResponseError.SERVICE_UNAVAILABLE.apply { actualResponse = "Request timed out" })
        } catch (e: ClientRequestException) {
            // Handle 4xx responses from Ktor
            logException(e)
            handleKtorResponseException(e)
        } catch (e: RedirectResponseException) {
            // Handle 3xx responses from Ktor
            logException(e)
            Resource.failure(error = ResponseError.BAD_REQUEST.apply { actualResponse = "Redirect error" })
        }
        catch (e: ServerResponseException) {
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
        }
        catch (e: IllegalArgumentException) {
            Log.e("SafeApiCall-caught", "IllegalArgumentException")
            logException(e)
            Resource.failure(error = ResponseError.BAD_REQUEST.apply { actualResponse = e.message })
        }
        catch (e: SocketTimeoutException) {
            logException(e)
            Resource.failure(error = ResponseError.SERVICE_UNAVAILABLE.apply { actualResponse = "Connection timed out" })
        } catch (e: UnknownHostException) {
            logException(e)
            Resource.failure(error = ResponseError.NETWORK_ERROR.apply { actualResponse = "Unable to resolve host" })
        } catch (e: ConnectException) {
            logException(e)
            Resource.failure(error = ResponseError.NETWORK_ERROR.apply { actualResponse = "Failed to connect to server" })
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
                is FileNotFoundException -> Resource.failure(error = ResponseError.FILE_NOT_FOUND.apply { actualResponse = e.message })
                else -> Resource.failure(error = ResponseError.NETWORK_ERROR.apply { actualResponse = e.message })
            }
        }
        catch (e: NoTransformationFoundException) {
            Log.e("SafeApiCall-caught", "NoTransformationFoundException")
            logException(e)

            Resource.failure(error = ResponseError.INTERNAL_SERVER_ERROR.apply {
                actualResponse = "Some error occurred. Error parsing server response."
            })
        }
        catch (e: JsonConvertException) {
            Log.e("SafeApiCall-caught", "JsonConvertException ${e.cause}")
            logException(e)

            Resource.failure(error = ResponseError.INTERNAL_SERVER_ERROR.apply {
                actualResponse = "Some error occurred. Error parsing server response."
            })
        }
        catch (e: Exception) {
            Log.e("SafeApiCall-caught", "Exception")
            logException(e)
            Resource.failure(error = ResponseError.UNKNOWN.apply { actualResponse = e.message })
        }
    }

    /**
     * Handle Ktor response exceptions by mapping to appropriate ResponseError types
     */
    suspend fun <T> handleKtorResponseException(e: ResponseException): Resource<T> {
        val errorBody = try { e.response.bodyAsText() } catch (_: Exception) { null }
        Log.e("SafeApiCall-Handle", "Error body: $errorBody")
        val errorResponse = parseErrorResponse(errorBody)
        Log.e("SafeApiCall-Handle", "Error response: $errorResponse")
        val errorMessage = errorResponse?.message ?: "Unknown error"

        val error = when (e.response.status.value) {
            400 -> ResponseError.BAD_REQUEST
            401 -> ResponseError.AUTH_HEADER_NOT_FOUND
            403 -> ResponseError.UNAUTHORISED
            404 -> ResponseError.DOES_NOT_EXIST
            429 -> ResponseError.RATE_LIMIT_EXCEEDED
            else -> ResponseError.UNKNOWN
        }.apply { actualResponse = errorMessage }

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
        Log.e("SafeApiCall", "API call failed with exception: ${e.javaClass.simpleName}, message: ${e.message}")
    }


    fun timestamp(pattern: String = "yyyy-MM-dd HH:mm:ss", date: Date = Date()): String = SimpleDateFormat(pattern, Locale.getDefault()).format(date)

    fun PIDGenerator(timeInMills: Long = System.currentTimeMillis(), name: String): String {
        val sanitizedName = name.replace(Regex("[^a-zA-Z0-9]"), "_")
        return "${sanitizedName}_$timeInMills"
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