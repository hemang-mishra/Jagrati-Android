package com.hexagraph.jagrati_android.repository.permission

import com.hexagraph.jagrati_android.model.permission.AssignRoleToPermissionRequest
import com.hexagraph.jagrati_android.model.permission.PermissionListResponse
import com.hexagraph.jagrati_android.model.permission.PermissionResponse
import com.hexagraph.jagrati_android.model.permission.PermissionRoleAssignmentResponse
import com.hexagraph.jagrati_android.model.permission.PermissionWithRolesListResponse
import com.hexagraph.jagrati_android.model.permission.RoleSummaryResponse
import com.hexagraph.jagrati_android.service.permission.KtorPermissionService
import com.hexagraph.jagrati_android.util.Resource
import com.hexagraph.jagrati_android.util.Utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementation of PermissionRepository using Ktor client.
 */
class KtorPermissionRepository(
    private val permissionService: KtorPermissionService
) : PermissionRepository {

    override suspend fun getAllPermissions(): Flow<Resource<PermissionListResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { permissionService.getAllPermissions() }
        emit(response)
    }

    override suspend fun getPermissionById(id: Long): Flow<Resource<PermissionResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { permissionService.getPermissionById(id) }
        emit(response)
    }

    override suspend fun assignRoleToPermission(permissionId: Long, roleId: Long): Flow<Resource<PermissionRoleAssignmentResponse>> = flow {
        emit(Resource.loading())
        val request = AssignRoleToPermissionRequest(permissionId = permissionId, roleId = roleId)
        val response = safeApiCall { permissionService.assignRoleToPermission(request) }
        emit(response)
    }

    override suspend fun removeRoleFromPermission(permissionId: Long, roleId: Long): Flow<Resource<PermissionRoleAssignmentResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { permissionService.removeRoleFromPermission(permissionId, roleId) }
        emit(response)
    }

    override suspend fun getAllPermissionsWithRoles(): Flow<Resource<PermissionWithRolesListResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { permissionService.getAllPermissionsWithRoles() }
        emit(response)
    }

    override suspend fun getRolesForPermission(permissionId: Long): Flow<Resource<List<RoleSummaryResponse>>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { permissionService.getRolesForPermission(permissionId) }
        emit(response)
    }
}
