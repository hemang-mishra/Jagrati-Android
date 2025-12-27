package com.hexagraph.jagrati_android.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.hexagraph.jagrati_android.model.User
import com.hexagraph.jagrati_android.model.permission.AllPermissions
import com.hexagraph.jagrati_android.model.permission.RoleSummaryResponse
import com.hexagraph.jagrati_android.model.user.UserSummaryDTO
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * A centralized class to manage DataStore preferences throughout the app using type-safe DataStorePreference pattern.
 */
class AppPreferences(
    val dataStore: DataStore<Preferences>
) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    companion object {

        // Auth related keys
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val FCM_TOKEN = stringPreferencesKey("fcm_token")

        // Permissions related key
        private val USER_PERMISSIONS = stringSetPreferencesKey("user_permissions")

        // User details and roles related keys
        private val USER_DETAILS = stringPreferencesKey("user_details")
        private val USER_ROLES = stringPreferencesKey("user_roles")
        private val LAST_SYNC_TIME = longPreferencesKey("last_sync_time")
        private val LAST_USED_TIME = longPreferencesKey("last_used_time")
        private val IS_VOLUNTEER = stringPreferencesKey("is_volunteer")

        // Draft keys
        private val STUDENT_REGISTRATION_DRAFT = stringPreferencesKey("student_registration_draft")
        private val VOLUNTEER_REGISTRATION_DRAFT = stringPreferencesKey("volunteer_registration_draft")
    }

    val lastSyncTime: DataStorePreference<Long> = object : DataStorePreference<Long> {
        override fun getFlow(): Flow<Long> =
            dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { it[LAST_SYNC_TIME] ?: 0L }
                .distinctUntilChanged()

        override suspend fun set(value: Long) {
            dataStore.edit { preferences ->
                preferences[LAST_SYNC_TIME] = value
            }
        }
    }

    val lastUsedTime: DataStorePreference<Long> = object : DataStorePreference<Long> {
        override fun getFlow(): Flow<Long> =
            dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { it[LAST_USED_TIME] ?: 0L }
                .distinctUntilChanged()

        override suspend fun set(value: Long) {
             dataStore.edit { preferences ->
                preferences[LAST_USED_TIME] = value
            }
        }
    }

    val isVolunteer: DataStorePreference<Boolean> = object : DataStorePreference<Boolean> {
        override fun getFlow(): Flow<Boolean> =
             dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { it[IS_VOLUNTEER]?.toBoolean() ?: false }
                .distinctUntilChanged()

        override suspend fun set(value: Boolean) {
             dataStore.edit { preferences ->
                preferences[IS_VOLUNTEER] = value.toString()
            }
        }
    }

    // Token Management
    val accessToken: DataStorePreference<String?> = object : DataStorePreference<String?> {
        override fun getFlow(): Flow<String?> =
             dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { it[ACCESS_TOKEN] }
                .distinctUntilChanged()

        override suspend fun set(value: String?) {
             dataStore.edit { preferences ->
                if (value == null) {
                    preferences.remove(ACCESS_TOKEN)
                } else {
                    preferences[ACCESS_TOKEN] = value
                }
            }
        }
    }

    val refreshToken: DataStorePreference<String?> = object : DataStorePreference<String?> {
        override fun getFlow(): Flow<String?> =
             dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { it[REFRESH_TOKEN] }
                .distinctUntilChanged()

        override suspend fun set(value: String?) {
             dataStore.edit { preferences ->
                if (value == null) {
                    preferences.remove(REFRESH_TOKEN)
                } else {
                    preferences[REFRESH_TOKEN] = value
                }
            }
        }
    }


    val fcmToken: DataStorePreference<String?> = object : DataStorePreference<String?> {
        override fun getFlow(): Flow<String?> =
             dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { it[FCM_TOKEN] }
                .distinctUntilChanged()

        override suspend fun set(value: String?) {
             dataStore.edit { preferences ->
                if (value == null) {
                    preferences.remove(FCM_TOKEN)
                } else {
                    preferences[FCM_TOKEN] = value
                }
            }
        }
    }


    suspend fun saveTokens(accessToken: String, refreshToken: String) {
         dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clearTokens() {
         dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
    }

    // Check if user is authenticated
    val isAuthenticated: DataStorePreference<Boolean> = object : DataStorePreference<Boolean> {
        override fun getFlow(): Flow<Boolean> =
             dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { it[ACCESS_TOKEN] != null }
                .distinctUntilChanged()

        override suspend fun set(value: Boolean) {
            // This is read-only and derived from tokens, so setting is not supported
            throw UnsupportedOperationException("Cannot directly set authentication status")
        }
    }

    // Permissions Management
    val userPermissions: DataStorePreference<Set<String>> = object : DataStorePreference<Set<String>> {
        override fun getFlow(): Flow<Set<String>> =
             dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { it[USER_PERMISSIONS] ?: emptySet() }
                .distinctUntilChanged()

        override suspend fun set(value: Set<String>) {
             dataStore.edit { preferences ->
                preferences[USER_PERMISSIONS] = value
            }
        }
    }

    fun hasPermission(permission: AllPermissions): Flow<Boolean> {
        return userPermissions.getFlow()
            .map { permissions -> permissions.contains(permission.name) }
            .distinctUntilChanged()
    }

    suspend fun saveUserPermissions(permissions: List<String>) {
        userPermissions.set(permissions.toSet())
    }

    suspend fun clearUserPermissions() {
        userPermissions.set(emptySet())
    }

    // User Details Management
    val userDetails: DataStorePreference<User?> = object : DataStorePreference<User?> {
        override fun getFlow(): Flow<User?> =
             dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { preferences ->
                    val userDetailsJson = preferences[USER_DETAILS] ?: return@map null
                    try {
                        json.decodeFromString<User>(userDetailsJson)
                    } catch (e: Exception) {
                        null
                    }
                }
                .distinctUntilChanged()

        override suspend fun set(value: User?) {
             dataStore.edit { preferences ->
                preferences[USER_DETAILS] = json.encodeToString(value)
            }
        }
    }

    suspend fun saveUserDetails(userDetails: UserSummaryDTO) {
         dataStore.edit { preferences ->
            preferences[USER_DETAILS] = json.encodeToString(userDetails.toUser())
        }
    }

    suspend fun clearUserDetails() {
         dataStore.edit { preferences ->
            preferences.remove(USER_DETAILS)
        }
    }

    // User Roles Management
    val userRoles: DataStorePreference<List<RoleSummaryResponse>> = object : DataStorePreference<List<RoleSummaryResponse>> {
        override fun getFlow(): Flow<List<RoleSummaryResponse>> =
             dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { preferences ->
                    val userRolesJson = preferences[USER_ROLES] ?: return@map emptyList()
                    try {
                        json.decodeFromString<List<RoleSummaryResponse>>(userRolesJson)
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
                .distinctUntilChanged()

        override suspend fun set(value: List<RoleSummaryResponse>) {
             dataStore.edit { preferences ->
                preferences[USER_ROLES] = json.encodeToString(value)
            }
        }
    }

    // Check if user has a specific role by name
    fun hasRole(roleName: String): Flow<Boolean> {
        return userRoles.getFlow()
            .map { roles -> roles.any { it.name == roleName } }
            .distinctUntilChanged()
    }

    suspend fun saveUserRoles(roles: List<RoleSummaryResponse>) {
        userRoles.set(roles)
    }

    suspend fun clearUserRoles() {
        userRoles.set(emptyList())
    }

    // Student Registration Draft
    suspend fun saveStudentRegistrationDraft(draft: StudentRegistrationDraft) {
         dataStore.edit { preferences ->
            preferences[STUDENT_REGISTRATION_DRAFT] = json.encodeToString(draft)
        }
    }

    suspend fun getStudentRegistrationDraft(): StudentRegistrationDraft? {
        return try {
            val jsonData =  dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { it[STUDENT_REGISTRATION_DRAFT] }
                .distinctUntilChanged()
                .first()
            if (jsonData != null) {
                json.decodeFromString<StudentRegistrationDraft>(jsonData)
            } else null
        } catch (e: Exception) {
            LogHelper.logError("AppPreferences", "Error getting student draft: ${e.message}", e)
            null
        }
    }

    suspend fun clearStudentRegistrationDraft() {
         dataStore.edit { preferences ->
            preferences.remove(STUDENT_REGISTRATION_DRAFT)
        }
    }

    // Volunteer Registration Draft
    suspend fun saveVolunteerRegistrationDraft(draft: VolunteerRegistrationDraft) {
         dataStore.edit { preferences ->
            preferences[VOLUNTEER_REGISTRATION_DRAFT] = json.encodeToString(draft)
        }
    }

    suspend fun getVolunteerRegistrationDraft(): VolunteerRegistrationDraft? {
        return try {
            val jsonData =  dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { it[VOLUNTEER_REGISTRATION_DRAFT] }
                .distinctUntilChanged()
                .first()
            if (jsonData != null) {
                json.decodeFromString<VolunteerRegistrationDraft>(jsonData)
            } else null
        } catch (e: Exception) {
            LogHelper.log("AppPreferences", "Error getting volunteer draft: ${e.message}")
            null
        }
    }

    suspend fun clearVolunteerRegistrationDraft() {
         dataStore.edit { preferences ->
            preferences.remove(VOLUNTEER_REGISTRATION_DRAFT)
        }
    }

    suspend fun clearAll() {
         dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

// Draft data classes
@Serializable
data class StudentRegistrationDraft(
    val firstName: String = "",
    val lastName: String = "",
    val gender: String? = null,
    val yearOfBirth: Int? = null,
    val schoolClass: String = "",
    val primaryContactNo: String = "",
    val secondaryContactNo: String = "",
    val fathersName: String = "",
    val mothersName: String = "",
    val selectedVillageId: Long? = null,
    val selectedGroupId: Long? = null
)

@Serializable
data class VolunteerRegistrationDraft(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val contactNo: String = "",
    val dateOfBirth: String? = null,
    val batch: String = "",
    val address: String = ""
)

