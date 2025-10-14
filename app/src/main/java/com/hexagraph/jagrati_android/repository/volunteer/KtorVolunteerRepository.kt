package com.hexagraph.jagrati_android.repository.volunteer

import com.hexagraph.jagrati_android.model.user.VolunteerDTO
import com.hexagraph.jagrati_android.model.volunteer.VolunteerListResponse
import com.hexagraph.jagrati_android.service.volunteer.KtorVolunteerService
import com.hexagraph.jagrati_android.util.Resource
import com.hexagraph.jagrati_android.util.Utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class KtorVolunteerRepository(
    private val volunteerService: KtorVolunteerService
) : VolunteerRepository {
    override suspend fun getAllVolunteers(): Flow<Resource<VolunteerListResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { volunteerService.getAllVolunteers() }
        emit(response)
    }

    override suspend fun getVolunteerByPid(pid: String): Flow<Resource<VolunteerDTO>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { volunteerService.getVolunteerByPid(pid) }
        emit(response)
    }
}

