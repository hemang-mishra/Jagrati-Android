package com.hexagraph.jagrati_android.ui.screens.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.model.AuthResult
import com.hexagraph.jagrati_android.repository.auth.AuthRepository
import com.hexagraph.jagrati_android.ui.navigation.AppNavigation
import com.hexagraph.jagrati_android.ui.screens.addStudent.AddStudentScreen
import com.hexagraph.jagrati_android.ui.screens.omniscan.OmniScanCameraScreen
import com.hexagraph.jagrati_android.ui.screens.omniscan.OmniScanMainScreen
import com.hexagraph.jagrati_android.ui.screens.omniscan.OmniScanUseCases
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val snackBarState = remember {
                SnackbarHostState()
            }

            JagratiAndroidTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { androidx.compose.material3.SnackbarHost(hostState = snackBarState) }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()
                        .padding(innerPadding)){
                        AppNavigation(
                            snackbarHostState = snackBarState,
                        )
                    }
                }
            }
        }
    }
}
