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
    suspend fun <T> safeApiCall(
        timeout: Long? = null,
        apiCall: suspend () -> T
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
            Resource.failure(error = ResponseError.SERVICE_UNAVAILABLE.apply { actualResponse = "Request timed out after ${timeout}ms" })
        } catch (e: HttpRequestTimeoutException) {
            logException(e)
            Resource.failure(error = ResponseError.SERVICE_UNAVAILABLE.apply { actualResponse = "Request timed out: ${e.message}" })
        } catch (e: ClientRequestException) {
            // Handle 4xx responses from Ktor
            logException(e)
            handleKtorResponseException(e)
        } catch (e: RedirectResponseException) {
            // Handle 3xx responses from Ktor
            logException(e)
            Resource.failure(error = ResponseError.BAD_REQUEST.apply { actualResponse = "Redirect error: ${e.message}" })
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
        } catch (e: SerializationException) {
            logException(e)
            Resource.failure(error = ResponseError.INTERNAL_SERVER_ERROR.apply { actualResponse = "Error parsing response: ${e.message}" })
        } catch (e: IllegalArgumentException) {
            logException(e)
            Resource.failure(error = ResponseError.BAD_REQUEST.apply { actualResponse = e.message })
        } catch (e: JsonSyntaxException) {
            logException(e)
            Resource.failure(error = ResponseError.INTERNAL_SERVER_ERROR.apply { actualResponse = "Error parsing server response" })
        } catch (e: SocketTimeoutException) {
            logException(e)
            Resource.failure(error = ResponseError.SERVICE_UNAVAILABLE.apply { actualResponse = "Connection timed out" })
        } catch (e: UnknownHostException) {
            logException(e)
            Resource.failure(error = ResponseError.NETWORK_ERROR.apply { actualResponse = "Unable to resolve host" })
        } catch (e: ConnectException) {
            logException(e)
            Resource.failure(error = ResponseError.NETWORK_ERROR.apply { actualResponse = "Failed to connect to server" })
        } catch (e: ResponseException) {
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
        } catch (e: Exception) {
            logException(e)
            Resource.failure(error = ResponseError.UNKNOWN.apply { actualResponse = e.message })
        }
    }

    /**
     * Handle Ktor response exceptions by mapping to appropriate ResponseError types
     */
    private suspend fun <T> handleKtorResponseException(e: ResponseException): Resource<T> {
        val errorBody = try {
            e.response.bodyAsText()
        } catch (e: Exception) {
            "Could not read error body"
        }

        return when (e.response.status.value) {
            400 -> ResponseError.BAD_REQUEST
            401 -> ResponseError.AUTH_HEADER_NOT_FOUND
            403 -> ResponseError.UNAUTHORISED
            404 -> ResponseError.DOES_NOT_EXIST
            429 -> ResponseError.RATE_LIMIT_EXCEEDED
            else -> ResponseError.UNKNOWN
        }.apply { actualResponse = errorBody }.let { error ->
            Resource.failure(error = error)
        }
    }

    /**
     * Log exception details and stack trace
     */
    private fun logException(e: Exception) {
        e.printStackTrace()
        Log.e("SafeApiCall", "API call failed with exception: ${e.javaClass.simpleName}, message: ${e.message}")
    }


    fun timestamp(pattern: String = "yyyy-MM-dd HH:mm:ss", date: Date = Date()): String = SimpleDateFormat(pattern, Locale.getDefault()).format(date)

    fun PIDGenerator(timeInMills: Long = System.currentTimeMillis(), name: String): String {
        val sanitizedName = name.replace(Regex("[^a-zA-Z0-9]"), "_")
        return "${sanitizedName}_$timeInMills"
    }
}