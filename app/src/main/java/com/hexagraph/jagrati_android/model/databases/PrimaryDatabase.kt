package com.hexagraph.jagrati_android.model.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hexagraph.jagrati_android.model.FaceEmbeddingsCacheEntity
import com.hexagraph.jagrati_android.model.FaceInfo
import com.hexagraph.jagrati_android.model.Groups
import com.hexagraph.jagrati_android.model.Student
import com.hexagraph.jagrati_android.model.Village
import com.hexagraph.jagrati_android.model.dao.EmbeddingsDAO
import com.hexagraph.jagrati_android.model.dao.FaceInfoDao
import com.hexagraph.jagrati_android.model.dao.GroupsDao
import com.hexagraph.jagrati_android.model.dao.StudentDao
import com.hexagraph.jagrati_android.model.dao.VillageDao


@Database(entities = [FaceInfo::class, Student::class, FaceEmbeddingsCacheEntity::class, Village::class, Groups::class], version = 1, exportSchema = false)
@TypeConverters(FaceInfoListConvertor::class, EmbeddingConvertor::class)
abstract class PrimaryDatabase: RoomDatabase() {
    abstract fun faceInfoDao(): FaceInfoDao
    abstract fun studentDetailsDao(): StudentDao
    abstract fun embeddingsDao(): EmbeddingsDAO
    abstract fun villageDao(): VillageDao
    abstract fun groupsDao(): GroupsDao
}