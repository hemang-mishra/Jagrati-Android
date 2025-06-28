package com.hexagraph.jagrati_android.di

import androidx.room.Room
import com.hexagraph.jagrati_android.model.databases.EmbeddingsDatabase
import com.hexagraph.jagrati_android.model.databases.PrimaryDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            PrimaryDatabase::class.java,
            "face_info_db"
        ).build()
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            EmbeddingsDatabase::class.java,
            "face_embedding_db"
        ).build()
    }

    single {
        get<PrimaryDatabase>().faceInfoDao()
    }

    single {
        get<PrimaryDatabase>().studentDetailsDao()
    }

    single {
        get<PrimaryDatabase>().attendanceDao()
    }

    single {
        get<EmbeddingsDatabase>().embeddingsDao()
    }
}