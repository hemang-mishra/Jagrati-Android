package com.hexagraph.jagrati_android.repository.role

import com.hexagraph.jagrati_android.model.role.CreateRoleRequest
import com.hexagraph.jagrati_android.model.role.RoleListResponse
import com.hexagraph.jagrati_android.model.role.RoleResponse
import com.hexagraph.jagrati_android.model.role.UpdateRoleRequest
import com.hexagraph.jagrati_android.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Interface for role management repository operations.
 */
interface RoleRepository {

    /**
     * Get all roles.
     *
     * @return Flow of Resource containing list of all roles
     */
    suspend fun getAllRoles(): Flow<Resource<RoleListResponse>>

    /**
     * Create a new role.
     *
     * @param name Role name
     * @param description Optional role description
     * @return Flow of Resource containing created role details
     */
    suspend fun createRole(name: String, description: String?): Flow<Resource<RoleResponse>>

    /**
     * Get role by ID.
     *
     * @param id Role ID
     * @return Flow of Resource containing role details
     */
    suspend fun getRoleById(id: Long): Flow<Resource<RoleResponse>>

    /**
     * Update an existing role.
     *
     * @param id Role ID
     * @param name New role name
     * @param description New role description
     * @return Flow of Resource containing updated role details
     */
    suspend fun updateRole(id: Long, name: String, description: String?): Flow<Resource<RoleResponse>>

    /**
     * Deactivate a role (soft delete).
     *
     * @param id Role ID to deactivate
     * @return Flow of Resource containing deactivated role details
     */
    suspend fun deactivateRole(id: Long): Flow<Resource<RoleResponse>>
}
