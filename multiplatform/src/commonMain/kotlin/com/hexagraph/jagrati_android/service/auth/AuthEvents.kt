package com.hexagraph.jagrati_android.service.auth

interface AuthEvents {
    suspend fun getDeviceId(): String
    suspend fun signOut()
}