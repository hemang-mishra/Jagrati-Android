package com.hexagraph.jagrati_android.model.databases

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.hexagraph.jagrati_android.model.ImageKitResponse
import kotlinx.serialization.json.Json

class ImageKitResponseConvertor {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromImageKitResponse(value: ImageKitResponse?): String? =
        try {
            value?.let { json.encodeToString(ImageKitResponse.serializer(), it) }
        } catch (e: Exception) {
            null
        }

    @TypeConverter
    fun toImageKitResponse(value: String?): ImageKitResponse? =
        if (value.isNullOrBlank()) null
        else try {
            json.decodeFromString(ImageKitResponse.serializer(), value)
        } catch (e: Exception) {
            null
        }
}