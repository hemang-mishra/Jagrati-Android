package com.hexagraph.jagrati_android.repository.role

import com.hexagraph.jagrati_android.model.role.CreateRoleRequest
import com.hexagraph.jagrati_android.model.role.RoleListResponse
import com.hexagraph.jagrati_android.model.role.RoleResponse
import com.hexagraph.jagrati_android.model.role.UpdateRoleRequest
import com.hexagraph.jagrati_android.service.role.KtorRoleService
import com.hexagraph.jagrati_android.util.Resource
import com.hexagraph.jagrati_android.util.Utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementation of RoleRepository using Ktor client.
 */
class KtorRoleRepository(
    private val roleService: KtorRoleService
) : RoleRepository {

    override suspend fun getAllRoles(): Flow<Resource<RoleListResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { roleService.getAllRoles() }
        emit(response)
    }

    override suspend fun createRole(name: String, description: String?): Flow<Resource<RoleResponse>> = flow {
        emit(Resource.loading())
        val request = CreateRoleRequest(name = name, description = description)
        val response = safeApiCall { roleService.createRole(request) }
        emit(response)
    }

    override suspend fun getRoleById(id: Long): Flow<Resource<RoleResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { roleService.getRoleById(id) }
        emit(response)
    }

    override suspend fun updateRole(id: Long, name: String, description: String?): Flow<Resource<RoleResponse>> = flow {
        emit(Resource.loading())
        val request = UpdateRoleRequest(id = id, name = name, description = description)
        val response = safeApiCall { roleService.updateRole(id, request) }
        emit(response)
    }

    override suspend fun deactivateRole(id: Long): Flow<Resource<RoleResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { roleService.deactivateRole(id) }
        emit(response)
    }
}
