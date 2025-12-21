package com.hexagraph.jagrati_android.repository.volunteer

import com.hexagraph.jagrati_android.model.user.VolunteerDTO
import com.hexagraph.jagrati_android.model.volunteer.UpdateVolunteerRequest
import com.hexagraph.jagrati_android.model.volunteer.VolunteerListResponse
import com.hexagraph.jagrati_android.util.Resource
import kotlinx.coroutines.flow.Flow

interface VolunteerRepository {
    suspend fun getAllVolunteers(): Flow<Resource<VolunteerListResponse>>

    suspend fun getVolunteerByPid(pid: String): Flow<Resource<VolunteerDTO>>

    suspend fun updateMyDetails(updateVolunteerRequest: UpdateVolunteerRequest): Flow<Resource<VolunteerDTO>>
}
