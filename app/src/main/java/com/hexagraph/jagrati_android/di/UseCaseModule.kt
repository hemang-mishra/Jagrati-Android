package com.hexagraph.jagrati_android.di

import com.hexagraph.jagrati_android.usecases.sync.DataSyncUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single{
        DataSyncUseCase(
            get(),
            get(),
            get()
        )
    }
}