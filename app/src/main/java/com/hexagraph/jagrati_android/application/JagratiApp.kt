package com.hexagraph.jagrati_android.application

import android.app.Application
import com.hexagraph.jagrati_android.di.databaseModule
import com.hexagraph.jagrati_android.di.networkModule
import com.hexagraph.jagrati_android.di.preferencesModule
import com.hexagraph.jagrati_android.di.repositoryModule
import com.hexagraph.jagrati_android.di.serviceModule
import com.hexagraph.jagrati_android.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class JagratiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
//            androidLogger()
            androidContext(this@JagratiApp)
            modules(viewModelModule,
                databaseModule,
                networkModule,
                preferencesModule,
                repositoryModule,
                serviceModule
            )
        }
    }
}