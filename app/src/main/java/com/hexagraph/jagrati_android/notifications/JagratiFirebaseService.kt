package com.hexagraph.jagrati_android.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject

class JagratiFirebaseService: FirebaseMessagingService() {
    private val notificationHelper by inject<NotificationHelper>()

    override fun onMessageReceived(message: RemoteMessage) {
        val remote = message.notification ?: return
        notificationHelper.showNotification(
            title = remote.title?: "Jagrati",
            message = remote.body ?: "",
            channelId = NotificationChannels.DEFAULT,
        )
    }

}