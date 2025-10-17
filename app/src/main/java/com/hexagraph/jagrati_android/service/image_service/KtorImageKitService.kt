package com.hexagraph.jagrati_android.service.image_service

import android.content.Context
import android.util.Log
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.model.ImageKitCredentials
import com.hexagraph.jagrati_android.model.ImageKitResponse
import com.hexagraph.jagrati_android.util.Utils
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import java.io.File

/**
 * Ktor client implementation for the ImageKit service.
 */
class KtorImageKitService(
    private val imageKitClient: HttpClient,
    private val serverClient: HttpClient,
    private val baseUrl: String
) : ImageKitService {

    private val TAG = "KtorImageKitService"
    private val UPLOAD_URL = "https://upload.imagekit.io/api/v1/files/upload"

    /**
     * Fetches ImageKit credentials from the backend
     *
     * @return ImageKit credentials for upload
     */
    override suspend fun getImageKitCredentials(): Result<ImageKitCredentials> {
        return try {
            val response = serverClient.get("$baseUrl/api/image-kit").body<ImageKitCredentials>()
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching ImageKit credentials: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Uploads an image file to ImageKit
     *
     * @param context Android context for accessing resources
     * @param file The file to upload
     * @return Result containing the ImageKit response
     */
    override suspend fun uploadImageFromFile(context: Context, file: File): Result<ImageKitResponse> {
        return try {
            // Get credentials from backend
            val credentialsResult = getImageKitCredentials()
            if (credentialsResult.isFailure) {
                return Result.failure(credentialsResult.exceptionOrNull()
                    ?: Exception("Failed to get ImageKit credentials"))
            }

            val credentials = credentialsResult.getOrNull()!!

            // The safeApiCall wrapper is assumed to handle exceptions and wrap the response
            val apiResponse = Utils.safeApiCall<ImageKitResponse> {
                imageKitClient.submitFormWithBinaryData(
                    url = UPLOAD_URL,
                    formData = formData {
                        append("file", file.readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                        })

                        append("fileName", file.name)
                        append("publicKey", context.getString(R.string.IMAGE_KIT_PUBLIC_KEY))
                        append("useUniqueFileName", "true")

                        // Use credentials from backend instead of hardcoded values
                        append("token", credentials.token)
                        append("expire", credentials.expire)
                        append("signature", credentials.signature)
                    }
                ).body()
            }

            if (apiResponse.isSuccess) {
                val response = apiResponse.data ?: ImageKitResponse()
                Log.d(TAG, "Image uploaded successfully: ${response.url}")
                Result.success(response)
            } else {
                Log.e(TAG, "Error uploading image: ${apiResponse.error?.actualResponse}")
                Result.failure(Exception(apiResponse.error?.actualResponse))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during image upload: ${e.message}")
            Result.failure(e)
        }
    }
}
