package com.hexagraph.jagrati_android.repository.auth

import com.hexagraph.jagrati_android.model.group.GroupListResponse
import com.hexagraph.jagrati_android.model.village.LongRequest
import com.hexagraph.jagrati_android.model.village.NameDescriptionRequest
import com.hexagraph.jagrati_android.service.auth.KtorGroupService
import com.hexagraph.jagrati_android.util.Resource
import com.hexagraph.jagrati_android.util.Utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class KtorGroupRepository(
    private val groupService: KtorGroupService
) : GroupRepository {
    override suspend fun addGroup(request: NameDescriptionRequest): Flow<Resource<String>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { groupService.addGroup(request) }
        emit(response)
    }

    override suspend fun removeGroup(request: LongRequest): Flow<Resource<String>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { groupService.removeGroup(request) }
        emit(response)
    }

    override suspend fun getAllActiveGroups(): Flow<Resource<GroupListResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { groupService.getAllActiveGroups() }
        emit(response)
    }
}

