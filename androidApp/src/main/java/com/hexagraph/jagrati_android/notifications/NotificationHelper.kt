package com.hexagraph.jagrati_android.notifications

import android.app.Notification
import android.content.Context
import androidx.annotation.DrawableRes

interface NotificationHelper {
    fun showNotification(
        title: String,
        message: String,
        channelId: String = NotificationChannels.DEFAULT,
        @DrawableRes iconRes: Int? = null,
        showIndeterminateProgress: Boolean = false,
        saveToDatabase: Boolean = true
    ): Int

    fun removeNotification(notificationId: Int)

    fun createChannels()

    fun showCustomNotification(
        context: Context,
        builder: Notification.Builder
    )
}