package com.hexagraph.jagrati_android.model.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hexagraph.jagrati_android.model.FaceInfo
import com.hexagraph.jagrati_android.model.StudentDetails
import com.hexagraph.jagrati_android.model.dao.FaceInfoDao
import com.hexagraph.jagrati_android.model.dao.StudentDetailsDao


@Database(entities = [FaceInfo::class, StudentDetails::class], version = 1, exportSchema = false)
@TypeConverters(FaceInfoListConvertor::class)
abstract class PrimaryDatabase: RoomDatabase() {
    abstract fun faceInfoDao(): FaceInfoDao
    abstract fun studentDetailsDao(): StudentDetailsDao
}