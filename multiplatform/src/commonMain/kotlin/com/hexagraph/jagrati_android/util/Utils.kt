package com.hexagraph.jagrati_android.util

import androidx.datastore.core.IOException
import com.hexagraph.jagrati_android.model.ResponseError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.serialization.JsonConvertException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import com.hexagraph.jagrati_android.BuildKonfig
import com.benasher44.uuid.uuid4
import io.ktor.client.network.sockets.SocketTimeoutException
import okio.FileNotFoundException
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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
            logException(e)
            Resource.failure(error = ResponseError.BAD_REQUEST.apply { actualResponse = e.message })
        } catch (e: SocketTimeoutException) {
            logException(e)
            Resource.failure(error = ResponseError.SERVICE_UNAVAILABLE.apply {
                actualResponse = "Connection timed out"
            })
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
                else -> Resource.failure(error = ResponseError.NETWORK_ERROR.apply {
                    actualResponse = e.message
                })
            }
        } catch (e: NoTransformationFoundException) {
            logException(e)

            Resource.failure(error = ResponseError.INTERNAL_SERVER_ERROR.apply {
                actualResponse = "Some error occurred. Error parsing server response."
            })
        } catch (e: JsonConvertException) {
            logException(e)

            Resource.failure(error = ResponseError.INTERNAL_SERVER_ERROR.apply {
                actualResponse = "Some error occurred. Error parsing server response."
            })
        } catch (e: Exception) {
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
        val errorResponse = parseErrorResponse(errorBody)
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
        LogHelper.logException(
            tag = "SafeApiCall",
            exception = e
        )
    }


//    fun timestamp(pattern: String = "yyyy-MM-dd HH:mm:ss", date: Date = Date()): String =
//        SimpleDateFormat(pattern, Locale.getDefault()).format(date)


    @OptIn(ExperimentalTime::class)
    fun generatePid(timeInMillis: Long = Clock.System.now().toEpochMilliseconds()): String{
        val uuid = uuid4().toString()
        return "${uuid}_$timeInMillis"
    }
    

    //Allowing debug versions to be treated as college email id
    fun isCollegeEmailId(email: String): Boolean{
        return email.endsWith("@iiitdmj.ac.in") || BuildKonfig.DEBUG
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