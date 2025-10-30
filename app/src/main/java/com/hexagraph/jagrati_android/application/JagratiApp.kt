package com.hexagraph.jagrati_android.application

import android.app.Application
import android.util.Log
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.util.CoilUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.hexagraph.jagrati_android.BuildConfig
import com.hexagraph.jagrati_android.di.databaseModule
import com.hexagraph.jagrati_android.di.networkModule
import com.hexagraph.jagrati_android.di.preferencesModule
import com.hexagraph.jagrati_android.di.repositoryModule
import com.hexagraph.jagrati_android.di.serviceModule
import com.hexagraph.jagrati_android.di.useCaseModule
import com.hexagraph.jagrati_android.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class JagratiApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase Crashlytics
        initializeCrashlytics()

        startKoin {
            androidContext(this@JagratiApp)
            modules(viewModelModule,
                databaseModule,
                networkModule,
                preferencesModule,
                repositoryModule,
                serviceModule,
                useCaseModule
            )
        }
    }

    private fun initializeCrashlytics() {
        try {
            val crashlytics = FirebaseCrashlytics.getInstance()

            // Enable/disable Crashlytics based on build type
            crashlytics.isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG

            // Log initialization
            if (BuildConfig.DEBUG) {
                Log.d("JagratiApp", "Crashlytics initialized (disabled in debug)")
            } else {
                Log.d("JagratiApp", "Crashlytics initialized and enabled")
            }

            // Set custom keys for better crash reporting
            crashlytics.setCustomKey("app_version", BuildConfig.VERSION_NAME)
            crashlytics.setCustomKey("version_code", BuildConfig.VERSION_CODE)

        } catch (e: Exception) {
            Log.e("JagratiApp", "Error initializing Crashlytics", e)
        }
    }
}