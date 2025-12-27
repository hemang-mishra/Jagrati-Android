package com.hexagraph.jagrati_android.repository.auth

import com.hexagraph.jagrati_android.model.group.GroupListResponse
import com.hexagraph.jagrati_android.model.village.LongRequest
import com.hexagraph.jagrati_android.model.village.NameDescriptionRequest
import com.hexagraph.jagrati_android.util.Resource
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    suspend fun addGroup(request: NameDescriptionRequest): Flow<Resource<String>>

    suspend fun removeGroup(request: LongRequest): Flow<Resource<String>>

    suspend fun getAllActiveGroups(): Flow<Resource<GroupListResponse>>
}

