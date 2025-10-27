package com.hexagraph.jagrati_android.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.hexagraph.jagrati_android.BuildConfig
import com.hexagraph.jagrati_android.notifications.NotificationHelper
import com.hexagraph.jagrati_android.notifications.NotificationHelperImpl
import com.hexagraph.jagrati_android.service.face_recognition.FaceRecognitionService
import com.hexagraph.jagrati_android.service.face_recognition.FaceRecognitionServiceImpl
import com.hexagraph.jagrati_android.service.image_service.ImageKitService
import com.hexagraph.jagrati_android.service.image_service.KtorImageKitService
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val serviceModule = module{
    single<NotificationHelper> {
        NotificationHelperImpl(androidApplication(), get())
    }

    single<FaceRecognitionService> {
        FaceRecognitionServiceImpl(get(),  get())
    }

    single<ImageKitService> {
        val context = get<Context>()
        val okhttpConfig : OkHttpConfig.()->Unit = {
            if(BuildConfig.DEBUG){
                addInterceptor(
                    ChuckerInterceptor.Builder(context)
                        .alwaysReadResponseBody(true)
                        .build()
                )
            }
        }
        val timeout = 50000L
        val client =HttpClient(io.ktor.client.engine.okhttp.OkHttp) {
            expectSuccess = true
            engine(okhttpConfig)
            install(HttpTimeout) {
                requestTimeoutMillis = timeout
                connectTimeoutMillis = timeout
                socketTimeoutMillis = timeout
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }

        KtorImageKitService(client,get(), androidApplication().getString(com.hexagraph.jagrati_android.R.string.BASE_URL))
    }
}