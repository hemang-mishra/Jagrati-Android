package com.hexagraph.jagrati_android.repository.user

import com.hexagraph.jagrati_android.model.user.UserDetailsWithRolesAndPermissions
import com.hexagraph.jagrati_android.model.user.UserRoleAssignmentResponse
import com.hexagraph.jagrati_android.model.user.UserWithRolesListResponse
import com.hexagraph.jagrati_android.model.user.UserWithRolesResponse
import com.hexagraph.jagrati_android.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Interface for user management repository operations.
 */
interface UserRepository {

    /**
     * Get all users with their assigned roles, optionally filtered by search term.
     *
     * @param search Optional search term to filter users
     * @return Flow of Resource containing list of users with their roles
     */
    suspend fun getAllUsers(search: String? = null): Flow<Resource<UserWithRolesListResponse>>

    /**
     * Get user details by PID.
     *
     * @param pid User PID
     * @return Flow of Resource containing user details with assigned roles
     */
    suspend fun getUserByPid(pid: String): Flow<Resource<UserWithRolesResponse>>

    /**
     * Assign a role to a user.
     *
     * @param userPid User PID
     * @param roleId Role ID
     * @return Flow of Resource containing assignment response
     */
    suspend fun assignRoleToUser(userPid: String, roleId: Long): Flow<Resource<UserRoleAssignmentResponse>>

    /**
     * Remove a role from a user.
     *
     * @param userPid User PID
     * @param roleId Role ID to remove
     * @return Flow of Resource containing removal response
     */
    suspend fun removeRoleFromUser(userPid: String, roleId: Long): Flow<Resource<UserRoleAssignmentResponse>>

    /**
     * Fetch permissions of the current authenticated user.
     *
     * @return Flow of Resource containing list of permissions for the current user
     */
    suspend fun getCurrentUserPermissions(timeMillis: Long): Flow<Resource<UserDetailsWithRolesAndPermissions>>

    /**
     * Delete a user by PID.
     *
     * @param pid User PID
     * @return Flow of Resource containing deletion response message
     */
    suspend fun deleteUser(pid: String): Flow<Resource<String>>
}
