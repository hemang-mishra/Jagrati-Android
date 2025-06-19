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
import com.hexagraph.jagrati_android.ui.navigation.AppNavigation
import com.hexagraph.jagrati_android.ui.screens.addStudent.AddStudentScreen
import com.hexagraph.jagrati_android.ui.screens.omniscan.OmniScanCameraScreen
import com.hexagraph.jagrati_android.ui.screens.omniscan.OmniScanMainScreen
import com.hexagraph.jagrati_android.ui.screens.omniscan.OmniScanUseCases
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var credentialManager: CredentialManager
    private lateinit var googleSignInLauncher: ActivityResultLauncher<IntentSenderRequest>
    private val googleIdToken = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize CredentialManager
        credentialManager = CredentialManager.create(this)

        // Set up Google Sign-In launcher
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val credential = result.data
                if (credential != null) {
                    handleGoogleSignInResult(credential)
                }
            }
        }

        setContent {
            val snackBarState = remember {
                SnackbarHostState()
            }
            val currentGoogleIdToken = remember { googleIdToken }

            JagratiAndroidTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { androidx.compose.material3.SnackbarHost(hostState = snackBarState) }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()
                        .padding(innerPadding)){
                        AppNavigation(
                            snackbarHostState = snackBarState,
                            onGoogleSignInClick = { signInWithGoogle() },
                            googleIdToken = currentGoogleIdToken.value
                        )
                    }
                }
            }
        }
    }

    private fun signInWithGoogle() {
        lifecycleScope.launch {
            try {
                // Configure the request
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    // Use your web client ID from Google Cloud Console
                    .setServerClientId(getString(R.string.WEB_CLIENT_ID))
                    .build()
                Log.i("Web client", "${getString(R.string.WEB_CLIENT_ID)} $googleIdOption")

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                // Request the credential
                val response = credentialManager.getCredential(
                    request = request,
                    context = this@MainActivity
                )

                handleGoogleSignInResponse(response)
            } catch (e: GetCredentialException) {
                Log.e("MainActivity", "Error getting credential: ${e.message}")
            }
        }
    }

    private fun handleGoogleSignInResponse(response: GetCredentialResponse) {
        val credential = response.credential
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        val idToken = googleIdTokenCredential.idToken

        // Store the ID token in the state
        googleIdToken.value = idToken
    }

    private fun handleGoogleSignInResult(data: Intent) {
        // This method is not needed with the Credential Manager API
        // The result is handled directly in the signInWithGoogle method
        Log.d("MainActivity", "Google Sign-In result received")
    }
}
