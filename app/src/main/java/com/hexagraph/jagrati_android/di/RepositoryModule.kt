package com.hexagraph.jagrati_android.di

import android.app.Application
import com.hexagraph.jagrati_android.repository.auth.AuthRepository
import com.hexagraph.jagrati_android.repository.auth.KtorAuthRepository
import com.hexagraph.jagrati_android.repository.auth.StudentRepository
import com.hexagraph.jagrati_android.repository.auth.KtorStudentRepository
import com.hexagraph.jagrati_android.repository.auth.AttendanceRepository
import com.hexagraph.jagrati_android.repository.auth.KtorAttendanceRepository
import com.hexagraph.jagrati_android.repository.auth.VillageRepository
import com.hexagraph.jagrati_android.repository.auth.KtorVillageRepository
import com.hexagraph.jagrati_android.repository.auth.GroupRepository
import com.hexagraph.jagrati_android.repository.auth.KtorGroupRepository
import com.hexagraph.jagrati_android.repository.auth.FaceDataRepository
import com.hexagraph.jagrati_android.repository.auth.KtorFaceDataRepository
import com.hexagraph.jagrati_android.repository.omniscan.OmniScanImplementation
import com.hexagraph.jagrati_android.repository.omniscan.OmniScanRepository
import com.hexagraph.jagrati_android.repository.permission.KtorPermissionRepository
import com.hexagraph.jagrati_android.repository.permission.PermissionRepository
import com.hexagraph.jagrati_android.repository.role.KtorRoleRepository
import com.hexagraph.jagrati_android.repository.role.RoleRepository
import com.hexagraph.jagrati_android.repository.user.KtorUserRepository
import com.hexagraph.jagrati_android.repository.user.UserRepository
import com.hexagraph.jagrati_android.repository.volunteer.KtorVolunteerRequestRepository
import com.hexagraph.jagrati_android.repository.volunteer.VolunteerRequestRepository
import com.hexagraph.jagrati_android.repository.volunteer.KtorVolunteerRepository
import com.hexagraph.jagrati_android.repository.volunteer.VolunteerRepository
import com.hexagraph.jagrati_android.repository.sync.SyncRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val repositoryModule = module {
    single<OmniScanRepository> { OmniScanImplementation(get(), androidApplication(), get()) }

    single<AuthRepository>{
        KtorAuthRepository(get(), get(), get())
    }
    single<StudentRepository> {
        KtorStudentRepository(get())
    }
    single<AttendanceRepository> {
        KtorAttendanceRepository(get())
    }
    single<VillageRepository> {
        KtorVillageRepository(get())
    }
    single<GroupRepository> {
        KtorGroupRepository(get())
    }
    single<FaceDataRepository> {
        KtorFaceDataRepository(get())
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
    single<VolunteerRepository> {
        KtorVolunteerRepository(get())
    }
    single { SyncRepository(
        studentDao = get(),
        volunteerDao = get(),
        villageDao = get(),
        groupsDao = get()
    ) }
}