package com.hexagraph.jagrati_android.repository.volunteer

import com.hexagraph.jagrati_android.model.volunteer.CreateVolunteerRequest
import com.hexagraph.jagrati_android.model.volunteer.DetailedVolunteerRequestListResponse
import com.hexagraph.jagrati_android.model.volunteer.MyVolunteerRequestListResponse
import com.hexagraph.jagrati_android.model.volunteer.VolunteerRequestActionResponse
import com.hexagraph.jagrati_android.service.volunteer.KtorVolunteerRequestService
import com.hexagraph.jagrati_android.util.Resource
import com.hexagraph.jagrati_android.util.Utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementation of VolunteerRequestRepository using Ktor client.
 */
class KtorVolunteerRequestRepository(
    private val volunteerRequestService: KtorVolunteerRequestService
) : VolunteerRequestRepository {

    override suspend fun createVolunteerRequest(request: CreateVolunteerRequest): Flow<Resource<VolunteerRequestActionResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { volunteerRequestService.createVolunteerRequest(request) }
        emit(response)
    }

    override suspend fun getAllVolunteerRequests(): Flow<Resource<DetailedVolunteerRequestListResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { volunteerRequestService.getAllVolunteerRequests() }
        emit(response)
    }

    override suspend fun approveVolunteerRequest(requestId: Long): Flow<Resource<VolunteerRequestActionResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { volunteerRequestService.approveVolunteerRequest(requestId) }
        emit(response)
    }

    override suspend fun rejectVolunteerRequest(requestId: Long, reason: String?): Flow<Resource<VolunteerRequestActionResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { volunteerRequestService.rejectVolunteerRequest(requestId, reason) }
        emit(response)
    }

    override suspend fun getMyVolunteerRequests(): Flow<Resource<MyVolunteerRequestListResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { volunteerRequestService.getMyVolunteerRequests() }
        emit(response)
    }
}
