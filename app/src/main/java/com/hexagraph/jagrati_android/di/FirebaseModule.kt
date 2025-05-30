package com.hexagraph.jagrati_android.di

import com.google.firebase.auth.FirebaseAuth
import com.hexagraph.jagrati_android.repository.auth.AuthRepository
import com.hexagraph.jagrati_android.repository.auth.FirebaseAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides Firebase-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /**
     * Provides the FirebaseAuth instance.
     * @return FirebaseAuth instance
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Provides the AuthRepository implementation.
     * @param firebaseAuthRepository The Firebase implementation of AuthRepository
     * @return AuthRepository instance
     */
    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuthRepository: FirebaseAuthRepository): AuthRepository = firebaseAuthRepository
}