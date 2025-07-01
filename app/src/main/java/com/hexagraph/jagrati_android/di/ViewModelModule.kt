package com.hexagraph.jagrati_android.di

import com.hexagraph.jagrati_android.ui.screens.addStudent.AddStudentViewModel
import com.hexagraph.jagrati_android.ui.screens.management.ManagementViewModel
import com.hexagraph.jagrati_android.ui.screens.omniscan.OmniScanViewModel
import com.hexagraph.jagrati_android.ui.screens.permissions.ManagePermissionsViewModel
import com.hexagraph.jagrati_android.ui.screens.permissions.PermissionDetailViewModel
import com.hexagraph.jagrati_android.ui.screens.roles.ManageRolesViewModel
import com.hexagraph.jagrati_android.ui.screens.studentAttendance.StudentAttendanceViewModel
import com.hexagraph.jagrati_android.ui.screens.userdetails.UserDetailsViewModel
import com.hexagraph.jagrati_android.ui.screens.userroles.UserDetailViewModel
import com.hexagraph.jagrati_android.ui.screens.userroles.UserRolesViewModel
import com.hexagraph.jagrati_android.ui.screens.volunteer.VolunteerRequestViewModel
import com.hexagraph.jagrati_android.ui.viewmodels.auth.AuthViewModel
import com.hexagraph.jagrati_android.ui.viewmodels.auth.ForgotPasswordViewModel
import com.hexagraph.jagrati_android.ui.viewmodels.auth.LoginViewModel
import com.hexagraph.jagrati_android.ui.viewmodels.auth.SignUpViewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory<AuthViewModel> { AuthViewModel(get()) }
    factory<LoginViewModel> { LoginViewModel(get()) }
    factory<SignUpViewModel> { SignUpViewModel(get()) }
    factory<ForgotPasswordViewModel>{ ForgotPasswordViewModel(get()) }
    factory<OmniScanViewModel> { OmniScanViewModel(get(), get(), get()) }
    factory<AddStudentViewModel> { AddStudentViewModel(get()) }
    factory<StudentAttendanceViewModel>{ StudentAttendanceViewModel(get(), get(), get()) }
    factory<UserDetailsViewModel> { UserDetailsViewModel(get(), get()) }
    factory<ManagementViewModel> { ManagementViewModel(get()) }
    factory<ManageRolesViewModel> { ManageRolesViewModel(get()) }

    // Permission management ViewModels
    factory { ManagePermissionsViewModel(get(), get()) }
    factory { (permissionId: Long) -> PermissionDetailViewModel(permissionId, get(), get()) }

    // User role management ViewModels
    factory { UserRolesViewModel(get()) }
    factory { (userPid: String) -> UserDetailViewModel(userPid, get(), get()) }

    // Volunteer management ViewModels
    factory { VolunteerRequestViewModel(get(), get()) }
}