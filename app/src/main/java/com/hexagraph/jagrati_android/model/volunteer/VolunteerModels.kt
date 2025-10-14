package com.hexagraph.jagrati_android.model.volunteer

import com.hexagraph.jagrati_android.model.user.VolunteerDTO
import kotlinx.serialization.Serializable

@Serializable
data class VolunteerListResponse(
    val volunteers: List<VolunteerDTO>
)

