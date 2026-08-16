package com.hexagraph.jagrati_android.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.hexagraph.jagrati_android.util.AppPreferences
import com.hexagraph.jagrati_android.util.AppUpdateHelper
import com.hexagraph.jagrati_android.util.UpdateCheckResult
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private const val FLEXIBLE_PROMPT_INTERVAL_MS = 24 * 60 * 60 * 1000L

@Composable
fun AppUpdateHandler(
    snackbarHostState: SnackbarHostState,
    updateHelper: AppUpdateHelper = koinInject(),
    appPreferences: AppPreferences = koinInject()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    val updateLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { }

    val promptRestart: suspend () -> Unit = remember(updateHelper, snackbarHostState) {
        {
            val result = snackbarHostState.showSnackbar(
                message = "Update downloaded and ready to install.",
                actionLabel = "Restart",
                duration = SnackbarDuration.Indefinite
            )
            if (result == SnackbarResult.ActionPerformed) {
                updateHelper.completeUpdate()
            }
        }
    }
    DisposableEffect(updateHelper) {
        val listener = InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                scope.launch { promptRestart() }
            }
        }
        updateHelper.registerInstallListener(listener)
        onDispose { updateHelper.unregisterInstallListener(listener) }
    }

    LaunchedEffect(Unit) {
        when (val result = updateHelper.checkForUpdate()) {
            is UpdateCheckResult.Available -> {
                if (result.recommendedType == AppUpdateType.IMMEDIATE) {
                    updateHelper.startUpdate(
                        result.appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        updateLauncher
                    )
                } else {
                    val lastPrompt = appPreferences.lastUpdatePromptTime.get()
                    val now = System.currentTimeMillis()
                    if (now - lastPrompt >= FLEXIBLE_PROMPT_INTERVAL_MS) {
                        val started = updateHelper.startUpdate(
                            result.appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            updateLauncher
                        )
                        if (started) {
                            appPreferences.lastUpdatePromptTime.set(now)
                        }
                    }
                }
            }

            is UpdateCheckResult.InProgress -> {
                updateHelper.startUpdate(
                    result.appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    updateLauncher
                )
            }

            UpdateCheckResult.Downloaded -> promptRestart()

            else -> Unit
        }
    }

    DisposableEffect(lifecycleOwner, updateHelper) {
        var skippedInitialResume = false
        val observer = LifecycleEventObserver { _, event ->
            if (event != Lifecycle.Event.ON_RESUME) return@LifecycleEventObserver
            if (!skippedInitialResume) {
                skippedInitialResume = true
                return@LifecycleEventObserver
            }
            scope.launch {
                when (val result = updateHelper.checkForUpdate()) {
                    is UpdateCheckResult.InProgress -> {
                        updateHelper.startUpdate(
                            result.appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            updateLauncher
                        )
                    }

                    UpdateCheckResult.Downloaded -> promptRestart()

                    else -> Unit
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}
