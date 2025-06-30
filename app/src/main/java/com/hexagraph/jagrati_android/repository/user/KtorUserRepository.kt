package com.hexagraph.jagrati_android.repository.user

import com.hexagraph.jagrati_android.api.AuthProvider
import com.hexagraph.jagrati_android.model.permission.PermissionListResponse
import com.hexagraph.jagrati_android.model.user.AssignRoleToUserRequest
import com.hexagraph.jagrati_android.model.user.UserDetailsWithRolesAndPermissions
import com.hexagraph.jagrati_android.model.user.UserRoleAssignmentResponse
import com.hexagraph.jagrati_android.model.user.UserWithRolesListResponse
import com.hexagraph.jagrati_android.model.user.UserWithRolesResponse
import com.hexagraph.jagrati_android.service.user.KtorUserService
import com.hexagraph.jagrati_android.util.Resource
import com.hexagraph.jagrati_android.util.Utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementation of UserRepository using Ktor client.
 */
class KtorUserRepository(
    private val userService: KtorUserService,
) : UserRepository {

    override suspend fun getAllUsers(search: String?): Flow<Resource<UserWithRolesListResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { userService.getAllUsers(search) }
        emit(response)
    }

    override suspend fun getUserByPid(pid: String): Flow<Resource<UserWithRolesResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { userService.getUserByPid(pid) }
        emit(response)
    }

    override suspend fun assignRoleToUser(userPid: String, roleId: Long): Flow<Resource<UserRoleAssignmentResponse>> = flow {
        emit(Resource.loading())
        val request = AssignRoleToUserRequest(userPid = userPid, roleId = roleId)
        val response = safeApiCall { userService.assignRoleToUser(request) }
        emit(response)
    }

    override suspend fun removeRoleFromUser(userPid: String, roleId: Long): Flow<Resource<UserRoleAssignmentResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { userService.removeRoleFromUser(userPid, roleId) }
        emit(response)
    }

    override suspend fun getCurrentUserPermissions(): Flow<Resource<UserDetailsWithRolesAndPermissions>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { userService.getCurrentUserDetails() }
        emit(response)
    }
}
