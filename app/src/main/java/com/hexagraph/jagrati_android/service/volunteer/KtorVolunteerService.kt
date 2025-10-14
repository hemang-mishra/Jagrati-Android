package com.hexagraph.jagrati_android.service.volunteer

import com.hexagraph.jagrati_android.model.user.VolunteerDTO
import com.hexagraph.jagrati_android.model.volunteer.VolunteerListResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class KtorVolunteerService(
    private val client: HttpClient,
    private val baseUrl: String
) {
    suspend fun getAllVolunteers(): VolunteerListResponse {
        return client.get("$baseUrl/api/volunteers").body()
    }

    suspend fun getVolunteerByPid(pid: String): VolunteerDTO {
        return client.get("$baseUrl/api/volunteers/$pid").body()
    }
}

