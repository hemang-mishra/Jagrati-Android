package com.hexagraph.jagrati_android.model.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hexagraph.jagrati_android.model.FaceEmbeddingsCacheEntity
import com.hexagraph.jagrati_android.model.FaceInfo
import com.hexagraph.jagrati_android.model.Groups
import com.hexagraph.jagrati_android.model.NotificationMessage
import com.hexagraph.jagrati_android.model.Student
import com.hexagraph.jagrati_android.model.Village
import com.hexagraph.jagrati_android.model.Volunteer
import com.hexagraph.jagrati_android.model.dao.EmbeddingsDAO
import com.hexagraph.jagrati_android.model.dao.FaceInfoDao
import com.hexagraph.jagrati_android.model.dao.GroupsDao
import com.hexagraph.jagrati_android.model.dao.NotificationDao
import com.hexagraph.jagrati_android.model.dao.StudentDao
import com.hexagraph.jagrati_android.model.dao.VillageDao
import com.hexagraph.jagrati_android.model.dao.VolunteerDao


@Database(entities = [FaceInfo::class, Student::class, FaceEmbeddingsCacheEntity::class, Village::class, Groups::class, Volunteer::class, NotificationMessage::class], version = 2, exportSchema = false)
@TypeConverters(FaceInfoListConvertor::class, EmbeddingConvertor::class, ImageKitResponseConvertor::class, NotificationTypeConvertor::class)
abstract class PrimaryDatabase: RoomDatabase() {
    abstract fun faceInfoDao(): FaceInfoDao
    abstract fun studentDetailsDao(): StudentDao
    abstract fun embeddingsDao(): EmbeddingsDAO
    abstract fun villageDao(): VillageDao
    abstract fun groupsDao(): GroupsDao
    abstract fun volunteerDao(): VolunteerDao
    abstract fun notificationDao(): NotificationDao

    fun clearAll() {
        clearAllTables()
    }
}