package com.hexagraph.jagrati_android.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


sealed interface Screens: NavKey {
    // Unified onboarding route
    @Serializable
    data object NavOnboardingRoute: Screens

    @Serializable
    data object NavPermissionsRoute: Screens

    // Authentication routes
    @Serializable
    data object NavLoginRoute: Screens

    @Serializable
    data object NavSignUpEmailRoute: Screens

    @Serializable
    data class NavSignUpDetailsRoute(
        val email: String
    ): Screens

    @Serializable
    data object NavForgotPasswordRoute: Screens

    @Serializable
    data class NavEmailVerificationRoute(
        val email: String
    ): Screens

    // User Details route (runs at app startup after authentication)
    @Serializable
    data object DetailsSyncRoute: Screens

    // Main app routes
    @Serializable
    data object NavHomeRoute: Screens


    // Management routes
    @Serializable
    data object NavManagementRoute: Screens

    //Manage roles and permissions
    @Serializable
    data object NavManageRolesRoute: Screens

    @Serializable
    data object NavManagePermissionsRoute: Screens

    @Serializable
    data class NavPermissionDetailRoute(
        val permissionId: Long
    ): Screens

    @Serializable
    data object NavUserRoleManagementRoute: Screens

    @Serializable
    data class NavUserDetailRoute(
        val userPid: String
    ): Screens

    @Serializable
    data object NavManageVolunteerRequestsRoute: Screens

    @Serializable
    data object NavMyVolunteerRequestsRoute: Screens

    @Serializable
    data object NavVillageManagementRoute: Screens

    @Serializable
    data object NavGroupManagementRoute: Screens

    // Volunteer routes
    @Serializable
    data object NavVolunteerRegistrationRoute: Screens

    @Serializable
    data object NavNonVolunteerHomeScreen: Screens

    // Student routes
    @Serializable
    data class NavStudentRegistrationRoute(
        val pid: String? = null
    ): Screens

    @Serializable
    data object NavStudentListRoute: Screens

    @Serializable
    data object NavVolunteerListRoute: Screens

    @Serializable
    data object NavUnifiedSearchRoute: Screens

    @Serializable
    data class NavUnifiedSearchAttendanceRoute(
        val dateMillis: Long
    ): Screens

    // Face data routes
    @Serializable
    data class NavFaceDataRegisterRoute(
        val pid: String
    ): Screens

    @Serializable
    data class NavStudentProfileRoute(
        val pid: String
    ): Screens

    @Serializable
    data class NavVolunteerProfileRoute(
        val pid: String
    ): Screens

    @Serializable
    data object NavCameraAttendanceMarkingRoute: Screens

    @Serializable
    data object NavAttendanceReportRoute: Screens

    @Serializable
    data object NavCameraSearchRoute: Screens

    @Serializable
    data class NavFullScreenImageRoute(
        val imageUrl: String,
        val imageName: String = "",
        val fileId: String = ""
    ): Screens
}