package com.hexagraph.jagrati_android.di

import com.hexagraph.jagrati_android.ui.screens.management.ManagementViewModel
import com.hexagraph.jagrati_android.ui.screens.permissions.ManagePermissionsViewModel
import com.hexagraph.jagrati_android.ui.screens.permissions.PermissionDetailViewModel
import com.hexagraph.jagrati_android.ui.screens.roles.ManageRolesViewModel
import com.hexagraph.jagrati_android.ui.screens.details_sync.DetailsSyncViewModel
import com.hexagraph.jagrati_android.ui.screens.student.StudentRegistrationViewModel
import com.hexagraph.jagrati_android.ui.screens.studentlist.StudentListViewModel
import com.hexagraph.jagrati_android.ui.screens.studentprofile.StudentProfileViewModel
import com.hexagraph.jagrati_android.ui.screens.volunteerlist.VolunteerListViewModel
import com.hexagraph.jagrati_android.ui.screens.search.UnifiedSearchViewModel
import com.hexagraph.jagrati_android.ui.screens.userroles.UserDetailViewModel
import com.hexagraph.jagrati_android.ui.screens.userroles.UserRolesViewModel
import com.hexagraph.jagrati_android.ui.screens.village.VillageManagementViewModel
import com.hexagraph.jagrati_android.ui.screens.group.GroupManagementViewModel
import com.hexagraph.jagrati_android.ui.screens.volunteer.VolunteerRequestViewModel
import com.hexagraph.jagrati_android.ui.screens.volunteer.manage.ManageVolunteerRequestsViewModel
import com.hexagraph.jagrati_android.ui.viewmodels.auth.AuthViewModel
import com.hexagraph.jagrati_android.ui.viewmodels.auth.ForgotPasswordViewModel
import com.hexagraph.jagrati_android.ui.viewmodels.auth.LoginViewModel
import com.hexagraph.jagrati_android.ui.viewmodels.auth.SignUpViewModel
import com.hexagraph.jagrati_android.ui.screens.facedata.FaceDataRegisterViewModel
import com.hexagraph.jagrati_android.ui.screens.attendance.AttendanceMarkingViewModel
import com.hexagraph.jagrati_android.ui.screens.attendancereport.AttendanceReportViewModel
import com.hexagraph.jagrati_android.ui.screens.myprofile.MyProfileViewModel
import com.hexagraph.jagrati_android.ui.screens.volunteerprofile.VolunteerProfileViewModel
import com.hexagraph.jagrati_android.ui.screens.attendanceview.AttendanceViewModel
import com.hexagraph.jagrati_android.ui.screens.editvolunteerprofile.EditVolunteerProfileViewModel
import com.hexagraph.jagrati_android.ui.viewmodels.AppViewModel
import org.koin.dsl.module

val viewModelModule = module {
    // Application-level ViewModel (scoped to Activity lifecycle)
    single { AppViewModel(get(), get()) }

    // Auth ViewModels
    factory<AuthViewModel> { AuthViewModel(get()) }
    factory<LoginViewModel> { LoginViewModel(get()) }
    factory<SignUpViewModel> { SignUpViewModel(get()) }
    factory<ForgotPasswordViewModel>{ ForgotPasswordViewModel(get()) }

    // Main app ViewModels
    factory<DetailsSyncViewModel> { DetailsSyncViewModel(get(), get(), get()) }
    factory<ManagementViewModel> { ManagementViewModel(get()) }
    factory<ManageRolesViewModel> { ManageRolesViewModel(get()) }

    // Permission management ViewModels
    factory { ManagePermissionsViewModel(get(), get()) }
    factory { (permissionId: Long) -> PermissionDetailViewModel(permissionId, get(), get()) }

    // User role management ViewModels
    factory { UserRolesViewModel(get()) }
    factory { (userPid: String) -> UserDetailViewModel(userPid, get(), get()) }

    factory { VillageManagementViewModel(get(), get()) }

    factory { GroupManagementViewModel(get(), get()) }

    // Volunteer management ViewModels
    factory { VolunteerRequestViewModel(get(), get()) }
    factory { ManageVolunteerRequestsViewModel(get()) }
    factory { VolunteerListViewModel(get()) }

    // Search ViewModels
    factory { UnifiedSearchViewModel(get(), get(), get(), get(), get()) }

    // Student management ViewModels
    factory { (pid: String?) -> StudentRegistrationViewModel(get(), get(), get(), get(), pid) }
    factory { StudentListViewModel(get(), get(), get()) }
    factory { (studentPid: String) -> StudentProfileViewModel(studentPid, get(), get(), get()) }

    factory { (volunteerPid: String) ->
        VolunteerProfileViewModel(
            volunteerPid,
            get(), // VolunteerRepository
            get(), // UserRepository
            get(),  // AttendanceRepository,
            get()
        )
    }

    factory { MyProfileViewModel(get(), get(), get()) }

    // Face data ViewModels

    // Attendance ViewModels
    factory { AttendanceMarkingViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { (pid: String) -> FaceDataRegisterViewModel(get(),  get(), get(), get(), get(),get(),get(),get(),pid) }
    factory { AttendanceReportViewModel(get(), get(), get(), get()) } // AttendanceRepository, AppPreferences, StudentDao, VolunteerDao
    factory { (pid: String, isStudent: Boolean) -> AttendanceViewModel(pid, isStudent, get(), get(), get()) }
    factory { (pid: String) -> EditVolunteerProfileViewModel(pid, get()) }
}