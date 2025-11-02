package com.hexagraph.jagrati_android.usecases.sync

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.permission.AllPermissions
import com.hexagraph.jagrati_android.model.user.UserDetailsWithRolesAndPermissions
import com.hexagraph.jagrati_android.repository.sync.SyncRepository
import com.hexagraph.jagrati_android.repository.user.UserRepository
import com.hexagraph.jagrati_android.util.AppPreferences
import com.hexagraph.jagrati_android.util.CrashlyticsHelper
import com.hexagraph.jagrati_android.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


const val syncTopic = "realtime-data-sync"
class DataSyncUseCase(
    private val userRepository: UserRepository,
    private val appPreferences: AppPreferences,
    private val syncRepository: SyncRepository
) {
    private val TAG = "DataSyncUseCase"

    fun syncDataInBackgroundAfterFCM(){
        CrashlyticsHelper.log(TAG, "FCM received for data sync, starting background sync")
        CoroutineScope(Dispatchers.IO).launch {
            val lastUsedTime = appPreferences.lastUsedTime.get()
            if(System.currentTimeMillis() - lastUsedTime > 30*60*1000){
                CrashlyticsHelper.log(TAG, "App not used in last 30 minutes, skipping data sync after FCM")
                return@launch
            }
            fetchUserDetails(
                onSuccessfulFetch = {
                    CrashlyticsHelper.log(TAG, "Data sync completed successfully after FCM")
                },
                onError = { error ->
                    CrashlyticsHelper.log(TAG, "Data sync failed after FCM: ${error.actualResponse}")
                }
            )
        }
    }

    fun subscribeToSyncTopic(){
        FirebaseMessaging.getInstance()
            .subscribeToTopic(syncTopic)
            .addOnSuccessListener {
                CrashlyticsHelper.log(TAG, "Subscribed to $syncTopic topic for real-time data sync")
            }
            .addOnFailureListener {
                CrashlyticsHelper.log(TAG, "Failed to subscribe to $syncTopic topic")
            }
    }

    fun unsubscribeFromSyncTopic(){
        FirebaseMessaging.getInstance()
            .unsubscribeFromTopic(syncTopic)
            .addOnSuccessListener {
                CrashlyticsHelper.log(TAG, "Unsubscribed from $syncTopic topic for real-time data sync")
            }
            .addOnFailureListener {
                CrashlyticsHelper.log(TAG, "Failed to unsubscribe from $syncTopic topic")
            }
    }
    suspend fun fetchUserDetails(
        onSuccessfulFetch: (UserDetailsWithRolesAndPermissions) -> Unit,
        onError: (ResponseError) -> Unit
    ){
        val lastSyncTime = appPreferences.lastSyncTime.get()
        userRepository.getCurrentUserPermissions(lastSyncTime).collect { result ->
            when (result.status){
                Resource.Status.SUCCESS -> {
                    if(result.data != null){
                        processUserData(result.data)
                        onSuccessfulFetch(result.data)
                    }
                    else{
                        onError(ResponseError.UNKNOWN.apply {  actualResponse="Received empty user data from server"})
                    }
                }

                Resource.Status.LOADING -> {
                    // Can be used to show loading state if needed
                }
                Resource.Status.FAILED -> {
                    val errorMsg = result.error ?: ResponseError.UNKNOWN.apply {  actualResponse="Unknown error occurred"}
                    onError(errorMsg)
                }
            }
        }
    }

    private suspend fun processUserData(data: UserDetailsWithRolesAndPermissions) {
        // Store user details in preferences
        appPreferences.saveUserDetails(data.userDetails)

        // Store user roles in preferences
        appPreferences.saveUserRoles(data.roles)
        appPreferences.isVolunteer.set(data.isVolunteer)

        // Map permission names to AllPermissions enum values
        val permissions = data.permissions.permissions.mapNotNull { permission ->
            try {
                // Try to find matching enum value by name
                AllPermissions.valueOf(permission.name)
                permission.name
            } catch (e: IllegalArgumentException) {
                // Skip permissions that don't match our enum
                null
            }
        }

        // Store permissions in AppPreferences
        appPreferences.saveUserPermissions(permissions)

        syncRepository.syncToLocalDb(data){
            // Avoiding non-volunteers to have last sync time as they don't have any data to sync
            if(data.isVolunteer) {
                appPreferences.lastSyncTime.set(System.currentTimeMillis())
            }
        }
    }
}