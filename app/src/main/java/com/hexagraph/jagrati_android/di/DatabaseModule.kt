package com.hexagraph.jagrati_android.di

import androidx.room.Room
import com.hexagraph.jagrati_android.model.databases.PrimaryDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            PrimaryDatabase::class.java,
            "primary_db"
        ).build()
    }

    single {
        get<PrimaryDatabase>().faceInfoDao()
    }

    single {
        get<PrimaryDatabase>().studentDetailsDao()
    }

    single {
        get<PrimaryDatabase>().embeddingsDao()
    }

    single {
        get<PrimaryDatabase>().groupsDao()
    }

    single {
        get<PrimaryDatabase>().villageDao()
    }

    single {
        get<PrimaryDatabase>().volunteerDao()
    }
}