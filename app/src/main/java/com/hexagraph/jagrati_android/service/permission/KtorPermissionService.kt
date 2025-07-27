package com.hexagraph.jagrati_android.service.permission

import com.hexagraph.jagrati_android.model.permission.AssignRoleToPermissionRequest
import com.hexagraph.jagrati_android.model.permission.PermissionListResponse
import com.hexagraph.jagrati_android.model.permission.PermissionResponse
import com.hexagraph.jagrati_android.model.permission.PermissionRoleAssignmentResponse
import com.hexagraph.jagrati_android.model.permission.PermissionWithRolesListResponse
import com.hexagraph.jagrati_android.model.permission.RoleSummaryResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Ktor client implementation for permission management API calls.
 */
class KtorPermissionService(
    private val client: HttpClient,
    private val baseUrl: String
) {

    /**
     * Get all permissions.
     *
     * @return List of all permissions
     */
    suspend fun getAllPermissions(): PermissionListResponse {
        return client.get("$baseUrl/api/permissions").body()
    }

    /**
     * Get permission by ID.
     *
     * @param id Permission ID
     * @return Permission details
     */
    suspend fun getPermissionById(id: Long): PermissionResponse {
        return client.get("$baseUrl/api/permissions/$id").body()
    }

    /**
     * Assign a role to a permission.
     *
     * @param request Role and permission association request
     * @return Assignment response
     */
    suspend fun assignRoleToPermission(request: AssignRoleToPermissionRequest): PermissionRoleAssignmentResponse {
        return client.post("$baseUrl/api/permissions/${request.permissionId}/roles") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Remove a role from a permission.
     *
     * @param permissionId Permission ID
     * @param roleId Role ID
     * @return Removal response
     */
    suspend fun removeRoleFromPermission(permissionId: Long, roleId: Long): PermissionRoleAssignmentResponse {
        return client.delete("$baseUrl/api/permissions/$permissionId/roles/$roleId").body()
    }

    /**
     * Get all permissions with their assigned roles.
     *
     * @return List of permissions with roles
     */
    suspend fun getAllPermissionsWithRoles(): PermissionWithRolesListResponse {
        return client.get("$baseUrl/api/permissions/with-roles").body()
    }

    /**
     * Get all roles assigned to a specific permission.
     *
     * @param permissionId Permission ID
     * @return List of roles
     */
    suspend fun getRolesForPermission(permissionId: Long): List<RoleSummaryResponse> {
        return client.get("$baseUrl/api/permissions/$permissionId/roles").body()
    }
}
