package com.hexagraph.jagrati_android.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val body: String,
    val type: NotificationType = NotificationType.TEXT,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val channelId: String? = null,
    val imageUrl: String? = null,
    val actionUrl: String? = null
)

