package com.hexagraph.jagrati_android.repository.volunteer

import com.hexagraph.jagrati_android.model.volunteer.CreateVolunteerRequest
import com.hexagraph.jagrati_android.model.volunteer.DetailedVolunteerRequestListResponse
import com.hexagraph.jagrati_android.model.volunteer.MyVolunteerRequestListResponse
import com.hexagraph.jagrati_android.model.volunteer.VolunteerRequestActionResponse
import com.hexagraph.jagrati_android.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Interface for volunteer request repository operations.
 */
interface VolunteerRequestRepository {

    /**
     * Create a new volunteer request.
     *
     * @param request Details of the volunteer request to create
     * @return Flow of Resource containing creation response
     */
    suspend fun createVolunteerRequest(request: CreateVolunteerRequest): Flow<Resource<VolunteerRequestActionResponse>>

    /**
     * Get all volunteer requests with detailed information.
     *
     * @return Flow of Resource containing list of all volunteer requests
     */
    suspend fun getAllVolunteerRequests(): Flow<Resource<DetailedVolunteerRequestListResponse>>

    /**
     * Approve a volunteer request.
     *
     * @param requestId ID of the request to approve
     * @return Flow of Resource containing approval response
     */
    suspend fun approveVolunteerRequest(requestId: Long): Flow<Resource<VolunteerRequestActionResponse>>

    /**
     * Reject a volunteer request.
     *
     * @param requestId ID of the request to reject
     * @param reason Optional reason for rejection
     * @return Flow of Resource containing rejection response
     */
    suspend fun rejectVolunteerRequest(requestId: Long, reason: String?): Flow<Resource<VolunteerRequestActionResponse>>

    /**
     * Get all volunteer requests made by the current authenticated user.
     *
     * @return Flow of Resource containing list of user's volunteer requests
     */
    suspend fun getMyVolunteerRequests(): Flow<Resource<MyVolunteerRequestListResponse>>
}
