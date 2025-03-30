package com.hexagraph.jagrati_android.di

import com.hexagraph.jagrati_android.repository.omniscan.OmniScanImplementation
import com.hexagraph.jagrati_android.repository.omniscan.OmniScanRepository
import com.hexagraph.jagrati_android.repository.student.AddStudentRepository
import com.hexagraph.jagrati_android.repository.student.AddStudentRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindOmniScanRepository(
        omniScanImplementation: OmniScanImplementation
    ): OmniScanRepository

    @Binds
    abstract fun bindAddStudentRepository(
        addStudentImplementation: AddStudentRepositoryImpl
    ): AddStudentRepository

}