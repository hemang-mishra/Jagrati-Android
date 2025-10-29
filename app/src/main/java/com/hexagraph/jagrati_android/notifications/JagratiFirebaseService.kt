package com.hexagraph.jagrati_android.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hexagraph.jagrati_android.usecases.sync.DataSyncUseCase
import org.koin.android.ext.android.inject

class JagratiFirebaseService: FirebaseMessagingService() {
    private val notificationHelper by inject<NotificationHelper>()
    private val syncUseCase by inject<DataSyncUseCase>()

    override fun onMessageReceived(message: RemoteMessage) {
        if(message.data.getOrDefault("Sync", "false") == "true"){
            syncUseCase.syncDataInBackgroundAfterFCM()
        }else {
            val title = message.data["title"] ?: return
            val body = message.data["message"] ?: return
            notificationHelper.showNotification(
                title = title,
                message = body,
                channelId = NotificationChannels.DEFAULT,
            )
        }
    }

}