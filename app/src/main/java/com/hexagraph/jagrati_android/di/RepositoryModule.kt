package com.hexagraph.jagrati_android.di

import com.hexagraph.jagrati_android.repository.auth.AuthRepository
import com.hexagraph.jagrati_android.repository.auth.KtorAuthRepository
import com.hexagraph.jagrati_android.repository.omniscan.OmniScanImplementation
import com.hexagraph.jagrati_android.repository.omniscan.OmniScanRepository
import com.hexagraph.jagrati_android.repository.permission.KtorPermissionRepository
import com.hexagraph.jagrati_android.repository.permission.PermissionRepository
import com.hexagraph.jagrati_android.repository.role.KtorRoleRepository
import com.hexagraph.jagrati_android.repository.role.RoleRepository
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
    single<PermissionRepository> {
        KtorPermissionRepository(get())
    }
    single<RoleRepository> {
        KtorRoleRepository(get())
    }
}