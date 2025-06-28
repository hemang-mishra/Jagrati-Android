package com.hexagraph.jagrati_android.di

import com.hexagraph.jagrati_android.util.AppPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val preferencesModule = module {
    single { AppPreferences(androidContext()) }
}
