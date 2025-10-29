package com.hexagraph.jagrati_android.ui.screens.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.hexagraph.jagrati_android.ui.navigation.AppNavigation
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.viewmodels.AppViewModel
import com.hexagraph.jagrati_android.util.AppPreferences
import com.hexagraph.jagrati_android.util.AppUpdateHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    private val updateHelper by lazy { AppUpdateHelper(this) }

    // Modern Activity Result API for handling in-app updates
    private val updateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        when (result.resultCode) {
            RESULT_OK -> {
                Toast.makeText(this, "Update installed successfully!", Toast.LENGTH_SHORT).show()
            }
            RESULT_CANCELED -> {
                Toast.makeText(this, "Update cancelled", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Update failed with code: ${result.resultCode}", Toast.LENGTH_SHORT).show()
                checkForUpdatesOnFailure()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val snackBarState = remember {
                SnackbarHostState()
            }

            val appViewModel: AppViewModel = koinViewModel()
            val shouldLogout by appViewModel.shouldLogout.collectAsStateWithLifecycle()

            JagratiAndroidTheme {
                AppNavigation(
                    snackbarHostState = snackBarState,
                    appViewModel = appViewModel,
                    shouldLogout = shouldLogout
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val appPreferences by inject<AppPreferences>()
        CoroutineScope(Dispatchers.Default).launch {
            appPreferences.lastUsedTime.set(System.currentTimeMillis())
            Log.d("MainActivity", "Updated last used time on resume")
        }

        // Check if there's an update in progress and resume it
        lifecycleScope.launch {
            try {
                val isInProgress = updateHelper.isUpdateInProgress()
                if (isInProgress) {
                    updateHelper.resumeUpdate(updateLauncher)
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error checking for in-progress update", e)
            }
        }
    }

    private fun checkForUpdatesOnFailure() {
        lifecycleScope.launch {
            try {
                val updateInfo = updateHelper.checkForUpdate()
                if (updateInfo != null) {
                    Toast.makeText(
                        this@MainActivity,
                        "An update is still available. Please try again from Settings.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error checking for updates on failure", e)
            }
        }
    }
}
