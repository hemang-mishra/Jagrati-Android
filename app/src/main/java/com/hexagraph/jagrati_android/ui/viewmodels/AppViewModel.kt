package com.hexagraph.jagrati_android.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.repository.auth.AuthRepository
import com.hexagraph.jagrati_android.util.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Application-level ViewModel that monitors critical app state throughout the lifecycle.
 * This ViewModel is scoped to the Activity and persists across configuration changes.
 */
class AppViewModel(
    private val appPreferences: AppPreferences,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _shouldLogout = MutableStateFlow(false)
    val shouldLogout: StateFlow<Boolean> = _shouldLogout.asStateFlow()

    private var hasInitialToken = false

    init {
        monitorRefreshToken()
    }

    /**
     * Monitors the refresh token and triggers logout when it becomes null or empty.
     * This ensures the user is logged out if the token is cleared due to errors or expiration.
     */
    private fun monitorRefreshToken() {
        viewModelScope.launch {
            appPreferences.refreshToken.getFlow().collectLatest { token ->
                Log.d("AppViewModel", "Refresh token changed: ${if (token.isNullOrEmpty()) "null/empty" else "present"}")

                // Only trigger logout if we had a token before and now it's gone
                if (hasInitialToken && token.isNullOrEmpty()) {
                    authRepository.signOut()
                    Log.w("AppViewModel", "Refresh token became null/empty - triggering logout")
                    _shouldLogout.value = true
                } else if (!token.isNullOrEmpty()) {
                    // Mark that we have a valid token
                    hasInitialToken = true
                    _shouldLogout.value = false
                }
            }
        }
    }

    /**
     * Resets the logout flag after handling the logout event.
     */
    fun onLogoutHandled() {
        _shouldLogout.value = false
        hasInitialToken = false
    }

    /**
     * Manually triggers logout and clears all preferences.
     */
    fun logout() {
        viewModelScope.launch {
            Log.d("AppViewModel", "Manual logout triggered")
            appPreferences.clearAll()
            _shouldLogout.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("AppViewModel", "AppViewModel cleared")
    }
}

