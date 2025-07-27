package com.hexagraph.jagrati_android.model.permission

import kotlinx.serialization.Serializable

@Serializable
data class PermissionResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val module: String,
    val action: String
)

@Serializable
data class PermissionListResponse(
    val permissions: List<PermissionResponse>
)

@Serializable
data class RoleSummaryResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val isActive: Boolean
)

@Serializable
data class PermissionRoleAssignmentResponse(
    val permissionId: Long,
    val roleId: Long,
    val message: String
)

@Serializable
data class PermissionWithRolesResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val module: String,
    val action: String,
    val assignedRoles: List<RoleSummaryResponse>
)

@Serializable
data class PermissionWithRolesListResponse(
    val permissions: List<PermissionWithRolesResponse>
)

@Serializable
data class AssignRoleToPermissionRequest(
    val permissionId: Long,
    val roleId: Long
)

@Serializable
data class RemoveRoleFromPermissionRequest(
    val permissionId: Long,
    val roleId: Long
)
