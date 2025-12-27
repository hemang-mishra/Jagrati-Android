package com.hexagraph.jagrati_android.service.auth

import com.google.firebase.messaging.FirebaseMessaging
import com.hexagraph.jagrati_android.model.databases.PrimaryDatabase
import com.hexagraph.jagrati_android.sync.DataSyncUseCase
import com.hexagraph.jagrati_android.util.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthEventsImpl(
    private val db: PrimaryDatabase,
    private val appPreferences: AppPreferences,
): AuthEvents {
    override suspend fun getDeviceId(): String {
        return appPreferences.fcmToken.get()?: FirebaseMessaging.getInstance().token.await()
    }

    override suspend fun signOut() {
        withContext(Dispatchers.Default) {
            db.clearAll()
        }
    }
}