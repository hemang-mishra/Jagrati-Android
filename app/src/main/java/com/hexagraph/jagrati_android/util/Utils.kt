package com.hexagraph.jagrati_android.util

import android.icu.text.SimpleDateFormat
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.ResponseError.Companion.getError
import retrofit2.Response
import java.io.FileNotFoundException
import java.io.IOException
import java.util.Date
import java.util.Locale

/**
 * A set of fairly general Android utility methods.
 */
object Utils {

    /** General function to parse an API endpoint's response.
     * @param request Call the API endpoint here. Run any pre-conditional checks to directly return error/success in some cases. */
    inline fun <T> parseResponse(request: () -> Response<T>): Resource<T> =
        runCatching<Resource<T>> {
            val response = request()

            return@runCatching if (response.isSuccessful) {
                Resource.success(response.body()!!)
            } else {
                val error = getError(response = response)
                Resource.failure(error = error)
            }

        }.getOrElse { logAndReturn(it) }

    fun <T> logAndReturn(it: Throwable): Resource<T> {
        it.printStackTrace()
        return when (it) {
            is FileNotFoundException -> Resource.failure(error = ResponseError.FILE_NOT_FOUND)
            is IOException -> Resource.failure(error = ResponseError.NETWORK_ERROR)
            else -> Resource.failure(error = ResponseError.UNKNOWN)
        }
    }

    /** Get human readable error.
     *
     * **CAUTION:** If this function is called once, calling it further with the same [Response] instance will result in an empty
     * string. Store this function's result for multiple use cases.*/
    fun <T> Response<T>.error(): String? = this.errorBody()?.string()


    fun timestamp(pattern: String = "yyyy-MM-dd HH:mm:ss", date: Date = Date()): String = SimpleDateFormat(pattern, Locale.getDefault()).format(date)

    fun PIDGenerator(timeInMills: Long = System.currentTimeMillis(), name: String): String{
        return "${name}_$timeInMills"
    }
}