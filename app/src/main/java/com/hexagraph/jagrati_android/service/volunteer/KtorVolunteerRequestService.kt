package com.hexagraph.jagrati_android.service.volunteer

import com.hexagraph.jagrati_android.model.volunteer.ApproveVolunteerRequest
import com.hexagraph.jagrati_android.model.volunteer.CreateVolunteerRequest
import com.hexagraph.jagrati_android.model.volunteer.DetailedVolunteerRequestListResponse
import com.hexagraph.jagrati_android.model.volunteer.MyVolunteerRequestListResponse
import com.hexagraph.jagrati_android.model.volunteer.RejectVolunteerRequest
import com.hexagraph.jagrati_android.model.volunteer.VolunteerRequestActionResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Ktor client implementation for volunteer request management API calls.
 */
class KtorVolunteerRequestService(
    private val client: HttpClient,
    private val baseUrl: String
) {

    /**
     * Create a new volunteer request.
     *
     * @param request The volunteer request details
     * @return Response with request ID and status
     */
    suspend fun createVolunteerRequest(request: CreateVolunteerRequest): VolunteerRequestActionResponse {
        return client.post("$baseUrl/api/volunteer-requests") {
            setBody(request)
        }.body()
    }

    /**
     * Get all volunteer requests.
     *
     * @return List of all volunteer requests with detailed information
     */
    suspend fun getAllVolunteerRequests(): DetailedVolunteerRequestListResponse {
        return client.get("$baseUrl/api/volunteer-requests").body()
    }

    /**
     * Approve a volunteer request.
     *
     * @param requestId The ID of the request to approve
     * @return Response with approval status
     */
    suspend fun approveVolunteerRequest(requestId: Long): VolunteerRequestActionResponse {
        val request = ApproveVolunteerRequest(requestId = requestId)
        return client.post("$baseUrl/api/volunteer-requests/$requestId/approve") {
            setBody(request)
        }.body()
    }

    /**
     * Reject a volunteer request.
     *
     * @param requestId The ID of the request to reject
     * @param reason Optional reason for rejection
     * @return Response with rejection status
     */
    suspend fun rejectVolunteerRequest(requestId: Long, reason: String?): VolunteerRequestActionResponse {
        val request = RejectVolunteerRequest(requestId = requestId, reason = reason)
        return client.post("$baseUrl/api/volunteer-requests/$requestId/reject") {
            setBody(request)
        }.body()
    }

    /**
     * Get volunteer requests submitted by the current authenticated user.
     *
     * @return List of volunteer requests made by current user
     */
    suspend fun getMyVolunteerRequests(): MyVolunteerRequestListResponse {
        return client.get("$baseUrl/api/volunteer-requests/my").body()
    }
}
