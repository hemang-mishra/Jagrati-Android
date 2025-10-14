package com.hexagraph.jagrati_android.repository.auth

import com.hexagraph.jagrati_android.model.village.LongRequest
import com.hexagraph.jagrati_android.model.village.StringRequest
import com.hexagraph.jagrati_android.model.village.VillageListResponse
import com.hexagraph.jagrati_android.util.Resource
import kotlinx.coroutines.flow.Flow

interface VillageRepository {
    suspend fun addVillage(village: StringRequest): Flow<Resource<String>>

    suspend fun removeVillage(villageId: LongRequest): Flow<Resource<String>>

    suspend fun getAllActiveVillages(): Flow<Resource<VillageListResponse>>
}

