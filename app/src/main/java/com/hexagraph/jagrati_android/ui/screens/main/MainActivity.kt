package com.hexagraph.jagrati_android.ui.screens.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hexagraph.jagrati_android.ui.navigation.AppNavigation
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.viewmodels.AppViewModel
import com.hexagraph.jagrati_android.util.AppPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
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
    }
}
