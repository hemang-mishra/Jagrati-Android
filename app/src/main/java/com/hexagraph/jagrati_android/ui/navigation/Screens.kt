package com.hexagraph.jagrati_android.ui.navigation

import kotlinx.serialization.Serializable


sealed class Screens{
    @Serializable
    data object NavHomeRoute

    @Serializable
    data object NavAttendanceRoute
}