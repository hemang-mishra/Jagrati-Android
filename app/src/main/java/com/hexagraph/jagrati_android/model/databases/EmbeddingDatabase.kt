package com.hexagraph.jagrati_android.model.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.hexagraph.jagrati_android.model.FaceEmbeddingsEntity
import com.hexagraph.jagrati_android.model.dao.EmbeddingsDAO

@Database(entities = [FaceEmbeddingsEntity::class], version = 1)
@TypeConverters(EmbeddingConvertor::class)
abstract class EmbeddingsDatabase: RoomDatabase(){
    abstract fun embeddingsDao(): EmbeddingsDAO
}


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