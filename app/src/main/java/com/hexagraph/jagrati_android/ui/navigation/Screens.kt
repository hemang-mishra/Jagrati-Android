package com.hexagraph.jagrati_android.ui.navigation

import kotlinx.serialization.Serializable


sealed class Screens{
    // Onboarding routes
    @Serializable
    data object NavOnboarding1Route

    @Serializable
    data object NavOnboarding2Route

    @Serializable
    data object NavOnboarding3Route

    @Serializable
    data object NavPermissionsRoute

    // Authentication routes
    @Serializable
    data object NavLoginRoute

    @Serializable
    data object NavSignUpEmailRoute

    @Serializable
    data object NavSignUpDetailsRoute {
        const val EMAIL_ARG = "email"
    }

    @Serializable
    data object NavForgotPasswordRoute

    @Serializable
    data object NavEmailVerificationRoute {
        const val EMAIL_ARG = "email"
    }

    // Main app routes
    @Serializable
    data object NavHomeRoute

    @Serializable
    data object NavAttendanceRoute
}
