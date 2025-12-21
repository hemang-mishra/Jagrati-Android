package com.hexagraph.jagrati_android.repository.permission

import com.hexagraph.jagrati_android.model.permission.AssignRoleToPermissionRequest
import com.hexagraph.jagrati_android.model.permission.PermissionListResponse
import com.hexagraph.jagrati_android.model.permission.PermissionResponse
import com.hexagraph.jagrati_android.model.permission.PermissionRoleAssignmentResponse
import com.hexagraph.jagrati_android.model.permission.PermissionWithRolesListResponse
import com.hexagraph.jagrati_android.model.permission.RoleSummaryResponse
import com.hexagraph.jagrati_android.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Interface for permission management repository operations.
 */
interface PermissionRepository {

    /**
     * Get all permissions.
     *
     * @return Flow of Resource containing list of all permissions
     */
    suspend fun getAllPermissions(): Flow<Resource<PermissionListResponse>>

    /**
     * Get permission by ID.
     *
     * @param id Permission ID
     * @return Flow of Resource containing permission details
     */
    suspend fun getPermissionById(id: Long): Flow<Resource<PermissionResponse>>

    /**
     * Assign a role to a permission.
     *
     * @param permissionId Permission ID
     * @param roleId Role ID
     * @return Flow of Resource containing assignment results
     */
    suspend fun assignRoleToPermission(permissionId: Long, roleId: Long): Flow<Resource<PermissionRoleAssignmentResponse>>

    /**
     * Remove a role from a permission.
     *
     * @param permissionId Permission ID
     * @param roleId Role ID
     * @return Flow of Resource containing removal results
     */
    suspend fun removeRoleFromPermission(permissionId: Long, roleId: Long): Flow<Resource<PermissionRoleAssignmentResponse>>

    /**
     * Get all permissions with their assigned roles.
     *
     * @return Flow of Resource containing list of permissions with roles
     */
    suspend fun getAllPermissionsWithRoles(): Flow<Resource<PermissionWithRolesListResponse>>

    /**
     * Get all roles assigned to a specific permission.
     *
     * @param permissionId Permission ID
     * @return Flow of Resource containing list of roles
     */
    suspend fun getRolesForPermission(permissionId: Long): Flow<Resource<List<RoleSummaryResponse>>>
}
