package com.hexagraph.jagrati_android.model.volunteer

import com.hexagraph.jagrati_android.util.LocalDateSerializer
import com.hexagraph.jagrati_android.util.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Response model for volunteer request action outcomes
 */
@Serializable
data class VolunteerRequestActionResponse(
    val requestId: Long,
    val status: String,
    val message: String
)

/**
 * Model for address information
 */
@Serializable
data class AddressDTO(
    val streetAddress1: String?,
    val streetAddress2: String?,
    val pincode: String?,
    val city: String?,
    val state: String?
)

/**
 * Model for user summary information
 */
@Serializable
data class UserSummaryDTO(
    val pid: String,
    val firstName: String,
    val lastName: String,
    val email: String
)

/**
 * Model for detailed volunteer request data
 */
@Serializable
data class DetailedVolunteerRequestResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val rollNumber: String?,
    val alternateEmail: String?,
    val batch: String?,
    val programme: String?,
    @Serializable(with = LocalDateSerializer::class)
    val dateOfBirth: LocalDate,
    val contactNumber: String?,
    val profileImageUrl: String?,
    val college: String?,
    val branch: String?,
    val yearOfStudy: Int?,
    val address: AddressDTO?,
    val status: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    val requestedByUser: UserSummaryDTO,
    val reviewedByUser: UserSummaryDTO?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val reviewedAt: LocalDateTime?
)

/**
 * Model containing list of detailed volunteer requests
 */
@Serializable
data class DetailedVolunteerRequestListResponse(
    val requests: List<DetailedVolunteerRequestResponse>
)

/**
 * Model for volunteer request summary data
 */
@Serializable
data class MyVolunteerRequestResponse(
    val id: Long,
    val status: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val reviewedAt: LocalDateTime?,
    val message: String?
)

/**
 * Model containing list of volunteer request summaries
 */
@Serializable
data class MyVolunteerRequestListResponse(
    val requests: List<MyVolunteerRequestResponse>
)

/**
 * Request model for creating a new volunteer request
 */
@Serializable
data class CreateVolunteerRequest(
    val firstName: String,
    val lastName: String,
    val gender: String,
    val rollNumber: String?,
    val alternateEmail: String?,
    val batch: String?,
    val programme: String?,
    val streetAddress1: String?,
    val streetAddress2: String?,
    val pincode: String?,
    val city: String?,
    val state: String?,
    val dateOfBirth: String,  // Will be parsed to LocalDate
    val contactNumber: String?,
    val profileImageUrl: String?,
    val college: String?,
    val branch: String?,
    val yearOfStudy: Int?
)

/**
 * Request model for approving a volunteer request
 */
@Serializable
data class ApproveVolunteerRequest(
    val requestId: Long
)

/**
 * Request model for rejecting a volunteer request
 */
@Serializable
data class RejectVolunteerRequest(
    val requestId: Long,
    val reason: String?
)
