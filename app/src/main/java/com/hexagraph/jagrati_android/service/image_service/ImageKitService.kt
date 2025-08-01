package com.hexagraph.jagrati_android.service.image_service

import android.content.Context
import com.hexagraph.jagrati_android.model.ImageKitCredentials
import com.hexagraph.jagrati_android.model.ImageKitResponse
import java.io.File

/**
 * Interface defining ImageKit service operations
 */
interface ImageKitService {
    /**
     * Fetches ImageKit credentials from the backend
     *
     * @return ImageKit credentials for upload
     */
    suspend fun getImageKitCredentials(): Result<ImageKitCredentials>

    /**
     * Uploads an image file to ImageKit
     *
     * @param context Android context for accessing resources
     * @param file The file to upload
     * @return Result containing the ImageKit response
     */
    suspend fun uploadImageFromFile(context: Context, file: File): Result<ImageKitResponse>
}
