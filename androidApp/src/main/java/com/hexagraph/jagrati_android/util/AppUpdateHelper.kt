package com.hexagraph.jagrati_android.util

import android.app.Activity
import android.content.IntentSender
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.tasks.await


class AppUpdateHelper(private val activity: Activity) {

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)


    suspend fun checkForUpdate(): AppUpdateInfo? {
        return try {
            val appUpdateInfo = appUpdateManager.appUpdateInfo.await()

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                appUpdateInfo
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUpdateAvailability(): Int {
        return try {
            val appUpdateInfo = appUpdateManager.appUpdateInfo.await()
            appUpdateInfo.updateAvailability()
        } catch (e: Exception) {
            UpdateAvailability.UNKNOWN
        }
    }

    suspend fun isUpdateInProgress(): Boolean {
        return try {
            val appUpdateInfo = appUpdateManager.appUpdateInfo.await()
            appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
        } catch (e: Exception) {
            false
        }
    }


    fun startImmediateUpdate(
        appUpdateInfo: AppUpdateInfo,
        updateLauncher: ActivityResultLauncher<IntentSenderRequest>
    ): Boolean {
        return try {
            val updateOptions = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()

            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                updateLauncher,
                updateOptions
            )
            true
        } catch (e: IntentSender.SendIntentException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun resumeUpdate(updateLauncher: ActivityResultLauncher<IntentSenderRequest>): Boolean {
        return try {
            val appUpdateInfo = appUpdateManager.appUpdateInfo.await()

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                val updateOptions = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    updateLauncher,
                    updateOptions
                )
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getAvailableVersionCode(): Int? {
        return try {
            val appUpdateInfo = appUpdateManager.appUpdateInfo.await()
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                appUpdateInfo.availableVersionCode()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        const val UPDATE_REQUEST_CODE = 1001
    }
}

