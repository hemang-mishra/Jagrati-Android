package com.hexagraph.jagrati_android.model.volunteer

import com.hexagraph.jagrati_android.model.Gender
import com.hexagraph.jagrati_android.model.ImageKitResponse
import com.hexagraph.jagrati_android.model.user.VolunteerDTO
import kotlinx.serialization.Serializable

@Serializable
data class VolunteerListResponse(
    val volunteers: List<VolunteerDTO>
)

@Serializable
data class UpdateVolunteerRequest(
    val rollNumber: String?,
    val firstName: String?,
    val lastName: String?,
    val gender: Gender?,
    val alternateEmail: String?,
    val batch: String?,
    val programme: String?,
    val streetAddress1: String?,
    val streetAddress2: String?,
    val pincode: String?,
    val city: String?,
    val state: String?,
    val dateOfBirth: String?,
    val contactNumber: String?,
    val college: String?,
    val branch: String?,
    val profilePic: ImageKitResponse?,
    val yearOfStudy: Int?
)
