package com.hexagraph.jagrati_android.di

import com.hexagraph.jagrati_android.sync.DataSyncUseCase
import com.hexagraph.jagrati_android.sync.DataSyncUseCaseImpl
import org.koin.dsl.module

val useCaseModule = module {
    single<DataSyncUseCase>{
        DataSyncUseCaseImpl(
            get(),
            get(),
            get()
        )
    }
}