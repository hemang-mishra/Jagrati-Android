package com.hexagraph.jagrati_android.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.hexagraph.jagrati_android.model.User
import com.hexagraph.jagrati_android.model.permission.AllPermissions
import com.hexagraph.jagrati_android.model.permission.RoleSummaryResponse
import com.hexagraph.jagrati_android.model.user.UserSummaryDTO

/**
 * A centralized class to manage DataStore preferences throughout the app.
 */
class AppPreferences(private val context: Context) {

    private val gson = Gson()

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "jagrati_prefs")

        // Auth related keys
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        // Permissions related key
        private val USER_PERMISSIONS = stringSetPreferencesKey("user_permissions")

        // User details and roles related keys
        private val USER_DETAILS = stringPreferencesKey("user_details")
        private val USER_ROLES = stringPreferencesKey("user_roles")
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



    // Permissions Management
    val userPermissions: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[USER_PERMISSIONS] ?: emptySet()
    }

    fun hasPermission(permission: AllPermissions): Flow<Boolean> {
        return userPermissions.map { permissions ->
            permissions.contains(permission.name)
        }
    }

    suspend fun saveUserPermissions(permissions: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[USER_PERMISSIONS] = permissions.toSet()
        }
    }

    suspend fun clearUserPermissions() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_PERMISSIONS)
        }
    }

    // User Details Management
    val userDetails: Flow<User?> = context.dataStore.data.map { preferences ->
        val userDetailsJson = preferences[USER_DETAILS] ?: return@map null
        try {
            gson.fromJson(userDetailsJson, UserSummaryDTO::class.java).toUser()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUserDetails(userDetails: UserSummaryDTO) {
        context.dataStore.edit { preferences ->
            preferences[USER_DETAILS] = gson.toJson(userDetails)
        }
    }

    suspend fun clearUserDetails() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_DETAILS)
        }
    }

    // User Roles Management
    val userRoles: Flow<List<RoleSummaryResponse>> = context.dataStore.data.map { preferences ->
        val userRolesJson = preferences[USER_ROLES] ?: return@map emptyList()
        try {
            val type = object : TypeToken<List<RoleSummaryResponse>>() {}.type
            gson.fromJson(userRolesJson, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveUserRoles(roles: List<RoleSummaryResponse>) {
        context.dataStore.edit { preferences ->
            preferences[USER_ROLES] = gson.toJson(roles)
        }
    }

    suspend fun clearUserRoles() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ROLES)
        }
    }

    // Clear all data
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
