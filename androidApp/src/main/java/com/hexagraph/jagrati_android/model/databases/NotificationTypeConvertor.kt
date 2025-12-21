package com.hexagraph.jagrati_android.model.databases

import androidx.room.TypeConverter
import com.hexagraph.jagrati_android.model.NotificationType

class NotificationTypeConvertor {
    @TypeConverter
    fun fromNotificationType(value: NotificationType): String {
        return value.name
    }

    @TypeConverter
    fun toNotificationType(value: String): NotificationType {
        return try {
            NotificationType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            NotificationType.TEXT
        }
    }
}

