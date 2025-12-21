package com.hexagraph.jagrati_android.model.repository

import com.hexagraph.jagrati_android.model.NotificationMessage
import com.hexagraph.jagrati_android.model.NotificationType
import com.hexagraph.jagrati_android.model.dao.NotificationDao
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val notificationDao: NotificationDao) {

    suspend fun insertNotification(notification: NotificationMessage) {
        notificationDao.insert(updateNotificationType(notification))
    }

    fun getAllNotifications(): Flow<List<NotificationMessage>> {
        return notificationDao.getAllNotifications()
    }

    fun getUnreadNotifications(): Flow<List<NotificationMessage>> {
        return notificationDao.getUnreadNotifications()
    }

    fun getUnreadCount(): Flow<Int> {
        return notificationDao.getUnreadCount()
    }

    suspend fun markAsRead(id: Int) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllAsRead() {
        notificationDao.markAllAsRead()
    }

    suspend fun deleteNotification(id: Int) {
        notificationDao.delete(id)
    }

    suspend fun deleteAllNotifications() {
        notificationDao.deleteAll()
    }

    suspend fun getNotificationById(id: Int): NotificationMessage? {
        return notificationDao.getNotificationById(id)
    }

    private fun updateNotificationType(notification: NotificationMessage): NotificationMessage{
        return if(notification.title.contains("you are now a volunteer") ||
            notification.title.contains("Request Rejected")){
            notification.copy(type = NotificationType.MY_VOLUNTEER_REQUEST_UPDATE)
        }else if(
            notification.title.contains("New Volunteer Request") ){
            notification.copy(type = NotificationType.NEW_VOLUNTEER_REQUEST)
        } else if(
            notification.title.contains("Thanks for volunteering") ){
            notification.copy(type = NotificationType.APPRECIATION_FOR_VOLUNTEERING)
        }
        else{
            notification
        }
    }
}

