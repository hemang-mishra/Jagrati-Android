package com.hexagraph.jagrati_android.model.databases

import androidx.room.TypeConverter
import kotlin.jvm.JvmStatic

object EmbeddingConvertor {

    @TypeConverter
    @JvmStatic
    fun fromFloatArray(value: FloatArray): String {
        return value.joinToString(",")
    }

    @TypeConverter
    @JvmStatic
    fun toFloatArray(value: String): FloatArray {
        return if (value.isEmpty()) FloatArray(0)
        else value.split(",").map { it.toFloat() }.toFloatArray()
    }
}