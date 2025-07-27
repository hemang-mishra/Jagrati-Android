package com.hexagraph.jagrati_android.service.role

import com.hexagraph.jagrati_android.model.role.CreateRoleRequest
import com.hexagraph.jagrati_android.model.role.DeactivateRoleRequest
import com.hexagraph.jagrati_android.model.role.RoleListResponse
import com.hexagraph.jagrati_android.model.role.RoleResponse
import com.hexagraph.jagrati_android.model.role.UpdateRoleRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Ktor client implementation for role management API calls.
 */
class KtorRoleService(
    private val client: HttpClient,
    private val baseUrl: String
) {

    /**
     * Get all roles.
     *
     * @return List of all roles
     */
    suspend fun getAllRoles(): RoleListResponse {
        return client.get("$baseUrl/api/roles").body()
    }

    /**
     * Create a new role.
     *
     * @param request Request with role details
     * @return Created role response
     */
    suspend fun createRole(request: CreateRoleRequest): RoleResponse {
        return client.post("$baseUrl/api/roles") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Get role by ID.
     *
     * @param id Role ID
     * @return Role details
     */
    suspend fun getRoleById(id: Long): RoleResponse {
        return client.get("$baseUrl/api/roles/$id").body()
    }

    /**
     * Update an existing role.
     *
     * @param id Role ID to update
     * @param request Updated role details
     * @return Updated role response
     */
    suspend fun updateRole(id: Long, request: UpdateRoleRequest): RoleResponse {
        return client.put("$baseUrl/api/roles/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Deactivate a role (soft delete).
     *
     * @param id Role ID to deactivate
     * @return Deactivated role response
     */
    suspend fun deactivateRole(id: Long): RoleResponse {
        return client.patch("$baseUrl/api/roles/$id/deactivate") {
            contentType(ContentType.Application.Json)
        }.body()
    }
}
