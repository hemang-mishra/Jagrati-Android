package com.hexagraph.jagrati_android.util

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.hexagraph.jagrati_android.BuildConfig
import kotlinx.coroutines.tasks.await

sealed interface UpdateCheckResult {
    data object NotAvailable : UpdateCheckResult

    data class Available(
        val appUpdateInfo: AppUpdateInfo,
        val availableVersionCode: Int,
        val priority: Int,
        val stalenessDays: Int,
        val recommendedType: Int
    ) : UpdateCheckResult

    data class InProgress(val appUpdateInfo: AppUpdateInfo) : UpdateCheckResult

    data object Downloaded : UpdateCheckResult

    data class Failed(val throwable: Throwable) : UpdateCheckResult
}

class AppUpdateHelper(context: Context) {

    private val appUpdateManager: AppUpdateManager =
        AppUpdateManagerFactory.create(context.applicationContext)

    suspend fun checkForUpdate(): UpdateCheckResult {
        if (BuildConfig.DEBUG) return UpdateCheckResult.NotAvailable

        return try {
            val info = appUpdateManager.appUpdateInfo.await()

            when {
                info.installStatus() == InstallStatus.DOWNLOADED -> UpdateCheckResult.Downloaded

                info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS ->
                    UpdateCheckResult.InProgress(info)

                info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE -> {
                    val priority = info.updatePriority()
                    val staleness = info.clientVersionStalenessDays() ?: 0
                    val type = recommendedType(info, priority, staleness)

                    if (type == null) {
                        UpdateCheckResult.NotAvailable
                    } else {
                        UpdateCheckResult.Available(
                            appUpdateInfo = info,
                            availableVersionCode = info.availableVersionCode(),
                            priority = priority,
                            stalenessDays = staleness,
                            recommendedType = type
                        )
                    }
                }

                else -> UpdateCheckResult.NotAvailable
            }
        } catch (e: Exception) {
            CrashlyticsHelper.logException(e, TAG)
            UpdateCheckResult.Failed(e)
        }
    }

    private fun recommendedType(info: AppUpdateInfo, priority: Int, stalenessDays: Int): Int? {
        val wantsImmediate = priority >= IMMEDIATE_PRIORITY_THRESHOLD ||
                (priority == ELEVATED_PRIORITY && stalenessDays >= ELEVATED_STALENESS_DAYS)

        val immediateAllowed = info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
        val flexibleAllowed = info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)

        return when {
            wantsImmediate && immediateAllowed -> AppUpdateType.IMMEDIATE
            flexibleAllowed -> AppUpdateType.FLEXIBLE
            immediateAllowed -> AppUpdateType.IMMEDIATE
            else -> null
        }
    }

    fun startUpdate(
        appUpdateInfo: AppUpdateInfo,
        updateType: Int,
        updateLauncher: ActivityResultLauncher<IntentSenderRequest>
    ): Boolean {
        return try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                updateLauncher,
                AppUpdateOptions.newBuilder(updateType).build()
            )
            true
        } catch (e: Exception) {
            CrashlyticsHelper.logException(e, TAG)
            false
        }
    }

    fun registerInstallListener(listener: InstallStateUpdatedListener) {
        appUpdateManager.registerListener(listener)
    }

    fun unregisterInstallListener(listener: InstallStateUpdatedListener) {
        appUpdateManager.unregisterListener(listener)
    }

    fun completeUpdate() {
        try {
            appUpdateManager.completeUpdate()
        } catch (e: Exception) {
            CrashlyticsHelper.logException(e, TAG)
        }
    }

    companion object {
        private const val TAG = "AppUpdateHelper"
        private const val IMMEDIATE_PRIORITY_THRESHOLD = 4
        private const val ELEVATED_PRIORITY = 3
        private const val ELEVATED_STALENESS_DAYS = 7
    }
}
