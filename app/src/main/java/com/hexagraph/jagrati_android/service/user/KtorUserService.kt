package com.hexagraph.jagrati_android.service.user

import com.hexagraph.jagrati_android.model.permission.PermissionListResponse
import com.hexagraph.jagrati_android.model.user.AssignRoleToUserRequest
import com.hexagraph.jagrati_android.model.user.RemoveRoleFromUserRequest
import com.hexagraph.jagrati_android.model.user.UserDetailsWithRolesAndPermissions
import com.hexagraph.jagrati_android.model.user.UserRoleAssignmentResponse
import com.hexagraph.jagrati_android.model.user.UserWithRolesListResponse
import com.hexagraph.jagrati_android.model.user.UserWithRolesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Ktor client implementation for user management API calls.
 */
class KtorUserService(
    private val client: HttpClient,
    private val baseUrl: String
) {

    /**
     * Get all users with their assigned roles, optionally filtered by search term.
     *
     * @param search Optional search term to filter users
     * @return List of users with their roles
     */
    suspend fun getAllUsers(search: String? = null): UserWithRolesListResponse {
        return client.get("$baseUrl/api/users") {
            search?.let { parameter("search", it) }
        }.body()
    }

    /**
     * Get user details by PID.
     *
     * @param pid User PID
     * @return User details with assigned roles
     */
    suspend fun getUserByPid(pid: String): UserWithRolesResponse {
        return client.get("$baseUrl/api/users/$pid").body()
    }

    /**
     * Assign a role to a user.
     *
     * @param request The assignment request containing user PID and role ID
     * @param authToken The authorization token
     * @return Assignment response
     */
    suspend fun assignRoleToUser(request: AssignRoleToUserRequest): UserRoleAssignmentResponse {
        return client.post("$baseUrl/api/users/${request.userPid}/roles") {
            setBody(request)
        }.body()
    }

    /**
     * Remove a role from a user.
     *
     * @param userPid User PID
     * @param roleId Role ID to remove
     * @return Removal response
     */
    suspend fun removeRoleFromUser(userPid: String, roleId: Long): UserRoleAssignmentResponse {
        return client.delete("$baseUrl/api/users/$userPid/roles/$roleId").body()
    }

    /**
     * Fetch details of the currently logged-in user, including their roles.
     *
     * @return User details with assigned roles and permissions
     */
    suspend fun getCurrentUserDetails(): UserDetailsWithRolesAndPermissions {
        return client.get("$baseUrl/api/users/me").body()
    }
}
