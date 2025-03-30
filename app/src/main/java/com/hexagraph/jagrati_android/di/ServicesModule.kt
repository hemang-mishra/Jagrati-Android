package com.hexagraph.jagrati_android.di

import com.hexagraph.jagrati_android.service.FaceRecognitionService
import com.hexagraph.jagrati_android.service.FaceRecognitionServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServicesModule {

    @Binds
    abstract fun bindFaceRecognitionService(
        faceRecognitionServiceImpl: FaceRecognitionServiceImpl
    ): FaceRecognitionService

}