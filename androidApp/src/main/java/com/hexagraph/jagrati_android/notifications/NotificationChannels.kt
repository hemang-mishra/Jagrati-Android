package com.hexagraph.jagrati_android.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {
    const val DEFAULT = "default_channel"
    const val ALERTS = "alerts_channel"
    const val SYNC = "sync_notifications_channel"

    fun createAll(context: Context){
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channels = listOf(
            NotificationChannel(
                DEFAULT,
                "General",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General app notifications"
            },

            NotificationChannel(
                ALERTS,
                "Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Important alerts and notifications"
                enableVibration(true)
            },

            NotificationChannel(
                SYNC,
                "Sync Notifications",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Low priority notifications"
            }

        )
        manager.createNotificationChannels(channels)
    }
}