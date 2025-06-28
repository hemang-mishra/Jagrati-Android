package com.hexagraph.jagrati_android.di

import com.hexagraph.jagrati_android.service.face_recognition.FaceRecognitionService
import com.hexagraph.jagrati_android.service.face_recognition.FaceRecognitionServiceImpl
import org.koin.dsl.module

val serviceModule = module{
    single<FaceRecognitionService> {
        FaceRecognitionServiceImpl(get())
    }

}