package com.hexagraph.jagrati_android.repository.auth

import com.hexagraph.jagrati_android.model.village.LongRequest
import com.hexagraph.jagrati_android.model.village.StringRequest
import com.hexagraph.jagrati_android.model.village.VillageListResponse
import com.hexagraph.jagrati_android.service.auth.KtorVillageService
import com.hexagraph.jagrati_android.util.Resource
import com.hexagraph.jagrati_android.util.Utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class KtorVillageRepository(
    private val villageService: KtorVillageService
) : VillageRepository {
    override suspend fun addVillage(village: StringRequest): Flow<Resource<String>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { villageService.addVillage(village) }
        emit(response)
    }

    override suspend fun removeVillage(villageId: LongRequest): Flow<Resource<String>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { villageService.removeVillage(villageId) }
        emit(response)
    }

    override suspend fun getAllActiveVillages(): Flow<Resource<VillageListResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { villageService.getAllActiveVillages() }
        emit(response)
    }
}

