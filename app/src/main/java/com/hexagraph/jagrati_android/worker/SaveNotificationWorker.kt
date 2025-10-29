package com.hexagraph.jagrati_android.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hexagraph.jagrati_android.model.NotificationMessage
import com.hexagraph.jagrati_android.model.NotificationType
import com.hexagraph.jagrati_android.model.repository.NotificationRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SaveNotificationWorker(
    private val context: Context,
    private val workParams: WorkerParameters
) : CoroutineWorker(context, workParams), KoinComponent {
    private val notificationRepository by inject<NotificationRepository>()

    override suspend fun doWork(): Result {
        return try {
            val title = inputData.getString("title") ?: return Result.failure()
            val message = inputData.getString("message") ?: return Result.failure()
            val channelId = inputData.getString("channelId") ?: "default_channel"
            Log.d("SaveNotificationWorker", "Saving notification: $title - $message")

            notificationRepository.insertNotification(
                NotificationMessage(
                    title = title,
                    body = message,
                    type = NotificationType.TEXT,
                    channelId = channelId,
                    timestamp = System.currentTimeMillis()
                )
            )
            Result.success()
        } catch (e: Exception) {
            Log.d("NotificationHelperImpl", "Error saving notification to database: ${e.message}")
            Result.retry()
        }

    }
}