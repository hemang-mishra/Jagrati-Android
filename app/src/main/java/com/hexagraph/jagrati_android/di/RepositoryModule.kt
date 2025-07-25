package com.hexagraph.jagrati_android.di

import com.hexagraph.jagrati_android.repository.auth.AuthRepository
import com.hexagraph.jagrati_android.repository.auth.KtorAuthRepository
import com.hexagraph.jagrati_android.repository.omniscan.OmniScanImplementation
import com.hexagraph.jagrati_android.repository.omniscan.OmniScanRepository
import com.hexagraph.jagrati_android.repository.student.AddStudentRepository
import com.hexagraph.jagrati_android.repository.student.AddStudentRepositoryImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val repositoryModule = module {
    single { androidApplication() }
    single<OmniScanRepository> { OmniScanImplementation(get(), get(), get()) }
    single<AddStudentRepository> {
        AddStudentRepositoryImpl(get())
    }
    single<AuthRepository>{
        KtorAuthRepository(get(), get())
    }
}