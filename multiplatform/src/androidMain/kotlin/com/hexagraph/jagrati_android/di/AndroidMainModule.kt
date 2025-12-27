package com.hexagraph.jagrati_android.di

import com.hexagraph.jagrati_android.util.AppPreferences
import com.hexagraph.jagrati_android.util.createDataStore
import org.koin.core.scope.get
import org.koin.dsl.module

val androidMainModule = module {
    single { AppPreferences(createDataStore(context = get())) }
}