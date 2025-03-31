package com.hexagraph.jagrati_android.ui.screens.main

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.hexagraph.jagrati_android.ui.navigation.AppNavigation
import com.hexagraph.jagrati_android.ui.screens.addStudent.AddStudentScreen
import com.hexagraph.jagrati_android.ui.screens.omniscan.OmniScanCameraScreen
import com.hexagraph.jagrati_android.ui.screens.omniscan.OmniScanMainScreen
import com.hexagraph.jagrati_android.ui.screens.omniscan.OmniScanUseCases
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val snackBarState= remember {
                SnackbarHostState()
            }
            JagratiAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()
                        .padding(innerPadding)){
                        AppNavigation(snackbarHostState = snackBarState)
                    }
                }
            }
        }
    }
}
