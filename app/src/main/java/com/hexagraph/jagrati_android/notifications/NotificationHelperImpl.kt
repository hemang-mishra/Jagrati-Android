package com.hexagraph.jagrati_android.notifications

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.ui.screens.main.MainActivity
import com.hexagraph.jagrati_android.model.NotificationMessage
import com.hexagraph.jagrati_android.model.NotificationType
import com.hexagraph.jagrati_android.model.repository.NotificationRepository
import com.hexagraph.jagrati_android.worker.SaveNotificationWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class NotificationHelperImpl(
    private val context: Context,
): NotificationHelper {
    private val notificationManager = NotificationManagerCompat.from(context)

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun showNotification(
        title: String,
        message: String,
        channelId: String,
        iconRes: Int?,
        showIndeterminateProgress: Boolean,
        saveToDatabase: Boolean
    ): Int {
        createChannels()
        Log.d("NotificationHelperImpl", "Showing notification: $title - $message")
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(iconRes ?: R.drawable.ic_notifications)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .apply {
                if (showIndeterminateProgress) {
                    setProgress(0, 0, true)
                }
            }
            .build()

        if(saveToDatabase) {
            val data = Data.Builder()
                .putString("title", title)
                .putString("message", message)
                .putString("channelId", channelId)
                .build()
            val workRequest = OneTimeWorkRequestBuilder<SaveNotificationWorker>()
                .setInputData(data)
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }

        val nid = Random.nextInt(10000)
        notificationManager.notify(nid, notification)

        return nid
    }

    override fun removeNotification(notificationId: Int){
        notificationManager.cancel(notificationId)
    }


    override fun createChannels() {
        NotificationChannels.createAll(context)
    }

    override fun showCustomNotification(
        context: Context,
        builder: Notification.Builder
    ) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val nid = Random.nextInt(10000)
        nm.notify(nid, builder.build())
    }
}