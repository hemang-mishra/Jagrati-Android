package com.hexagraph.jagrati_android.di

import android.content.Context
import android.content.SharedPreferences
import com.hexagraph.jagrati_android.repository.auth.AuthRepository
import com.hexagraph.jagrati_android.repository.auth.SpringAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides authentication-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    private const val PREFS_NAME = "auth_prefs"

    /**
     * Provides SharedPreferences for storing authentication data.
     */
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Provides the AuthRepository implementation.
     * @param springAuthRepository The Spring Boot implementation of AuthRepository
     * @return AuthRepository instance
     */
    @Provides
    @Singleton
    fun provideAuthRepository(springAuthRepository: SpringAuthRepository): AuthRepository = springAuthRepository
}