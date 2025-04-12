package com.hexagraph.jagrati_android.di

import android.content.Context
import androidx.room.Room
import com.hexagraph.jagrati_android.model.databases.FaceInfoListConvertor
import com.hexagraph.jagrati_android.model.databases.PrimaryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun providesFaceInfoDatabase(
        @ApplicationContext context: Context
    ): PrimaryDatabase = Room.databaseBuilder(
        context,
        PrimaryDatabase::class.java,
        "face_info_db"
    )
        .build()

    @Provides
    @Singleton
    fun provideFaceInfoDao(
        database: PrimaryDatabase
    ) = database.faceInfoDao()

    @Provides
    @Singleton
    fun provideStudentDetailsDao(
        database: PrimaryDatabase
    ) = database.studentDetailsDao()

    @Provides
    @Singleton
    fun provideAttendanceDao(
        database: PrimaryDatabase
    ) = database.attendanceDao()
}