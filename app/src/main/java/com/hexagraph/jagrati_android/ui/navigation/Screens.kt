package com.hexagraph.jagrati_android.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


sealed interface Screens: NavKey {
    // Onboarding routes
    @Serializable
    data object NavOnboarding1Route: Screens

    @Serializable
    data object NavOnboarding2Route: Screens

    @Serializable
    data object NavOnboarding3Route: Screens

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
    data object NavUserDetailsRoute: Screens

    // Main app routes
    @Serializable
    data object NavHomeRoute: Screens

    @Serializable
    data object NavAttendanceRoute: Screens
}
