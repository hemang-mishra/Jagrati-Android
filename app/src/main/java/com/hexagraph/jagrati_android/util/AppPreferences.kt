package com.hexagraph.jagrati_android.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.hexagraph.jagrati_android.model.User

/**
 * A centralized class to manage DataStore preferences throughout the app.
 */
class AppPreferences(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "jagrati_prefs")

        // Auth related keys
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_DISPLAY_NAME = stringPreferencesKey("user_display_name")
        private val USER_EMAIL_VERIFIED = booleanPreferencesKey("user_email_verified")
        private val USER_PHOTO_URL = stringPreferencesKey("user_photo_url")
    }

    // Token Management
    val accessToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN]
    }

    val refreshToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN]
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
    }

    // Check if user is authenticated
    fun isAuthenticated(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN] != null
    }

    // User Info Management
    val currentUser: Flow<User?> = context.dataStore.data.map { preferences ->
        val userId = preferences[USER_ID] ?: return@map null

        User(
            uid = userId,
            email = preferences[USER_EMAIL] ?: "",
            displayName = preferences[USER_DISPLAY_NAME] ?: "",
            isEmailVerified = preferences[USER_EMAIL_VERIFIED] ?: false,
            photoUrl = preferences[USER_PHOTO_URL] ?: ""
        )
    }

    suspend fun saveUserInfo(user: User) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = user.uid
            preferences[USER_EMAIL] = user.email
            preferences[USER_DISPLAY_NAME] = user.displayName
            preferences[USER_EMAIL_VERIFIED] = user.isEmailVerified
            preferences[USER_PHOTO_URL] = user.photoUrl ?: ""
        }
    }

    suspend fun clearUserInfo() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID)
            preferences.remove(USER_EMAIL)
            preferences.remove(USER_DISPLAY_NAME)
            preferences.remove(USER_EMAIL_VERIFIED)
            preferences.remove(USER_PHOTO_URL)
        }
    }

    // Clear all data
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
