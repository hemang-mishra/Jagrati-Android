package com.hexagraph.jagrati_android.di

import android.app.Application
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
import com.hexagraph.jagrati_android.repository.user.KtorUserRepository
import com.hexagraph.jagrati_android.repository.user.UserRepository
import com.hexagraph.jagrati_android.repository.volunteer.KtorVolunteerRequestRepository
import com.hexagraph.jagrati_android.repository.volunteer.VolunteerRequestRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val repositoryModule = module {
    single<OmniScanRepository> { OmniScanImplementation(get(), androidApplication(), get()) }
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
    single<UserRepository> {
        KtorUserRepository(get())
    }
    single<VolunteerRequestRepository> {
        KtorVolunteerRequestRepository(get())
    }
}