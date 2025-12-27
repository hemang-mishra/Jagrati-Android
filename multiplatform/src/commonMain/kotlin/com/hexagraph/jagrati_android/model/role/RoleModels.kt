package com.hexagraph.jagrati_android.model.role

import kotlinx.serialization.Serializable

/**
 * Response model for a role.
 */
@Serializable
data class RoleResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val isActive: Boolean
)

/**
 * Response model for a list of roles.
 */
@Serializable
data class RoleListResponse(
    val roles: List<RoleResponse>
)

/**
 * Request model for creating a new role.
 */
@Serializable
data class CreateRoleRequest(
    val name: String,
    val description: String?
)

/**
 * Request model for updating an existing role.
 */
@Serializable
data class UpdateRoleRequest(
    val id: Long,
    val name: String,
    val description: String?
)

/**
 * Request model for deactivating a role.
 */
@Serializable
data class DeactivateRoleRequest(
    val id: Long
)
