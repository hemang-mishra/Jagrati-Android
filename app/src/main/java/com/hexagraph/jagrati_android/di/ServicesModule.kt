package com.hexagraph.jagrati_android.di

import com.google.firebase.auth.FirebaseAuth
import com.hexagraph.jagrati_android.model.dao.EmbeddingsDAO
import com.hexagraph.jagrati_android.service.auth.FirebaseAuthService
import com.hexagraph.jagrati_android.service.face_recognition.FaceRecognitionService
import com.hexagraph.jagrati_android.service.face_recognition.FaceRecognitionServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServicesModule {

    @Provides
    @Singleton
    fun providesFaceRecognitionService(
        faceEmbeddingsDAO: EmbeddingsDAO
    ): FaceRecognitionService{
        return FaceRecognitionServiceImpl(faceEmbeddingsDAO)
    }

    @Provides
    @Singleton
    fun providesFirebaseAuth(): FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun providesFirebaseAuthService(
        firebaseAuth: FirebaseAuth
    ): FirebaseAuthService{
        return FirebaseAuthService(firebaseAuth)
    }
}