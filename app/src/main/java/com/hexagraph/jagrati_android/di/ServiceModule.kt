package com.hexagraph.jagrati_android.di

import com.hexagraph.jagrati_android.service.face_recognition.FaceRecognitionService
import com.hexagraph.jagrati_android.service.face_recognition.FaceRecognitionServiceImpl
import com.hexagraph.jagrati_android.service.image_service.ImageKitService
import com.hexagraph.jagrati_android.service.image_service.KtorImageKitService
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val serviceModule = module{
    single<FaceRecognitionService> {
        FaceRecognitionServiceImpl(get(),  get())
    }

    single<ImageKitService> {
        KtorImageKitService(get(), androidApplication().getString(com.hexagraph.jagrati_android.R.string.BASE_URL))
    }
}