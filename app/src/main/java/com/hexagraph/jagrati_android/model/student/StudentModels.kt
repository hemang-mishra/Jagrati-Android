package com.hexagraph.jagrati_android.model.student

import kotlinx.serialization.Serializable

@Serializable
data class StudentRequest(
    val pid: String,
    val firstName: String,
    val lastName: String,
    val yearOfBirth: Int? = null,
    val gender: String,
    val profilePic: String? = null,
    val schoolClass: String? = null,
    val villageId: Long,
    val groupId: Long,
    val primaryContactNo: String? = null,
    val secondaryContactNo: String? = null,
    val fathersName: String? = null,
    val mothersName: String? = null
)

@Serializable
data class UpdateStudentRequest(
    val pid: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val yearOfBirth: Int? = null,
    val gender: String? = null,
    val profilePic: String? = null,
    val schoolClass: String? = null,
    val villageId: Long? = null,
    val groupId: Long? = null,
    val primaryContactNo: String? = null,
    val secondaryContactNo: String? = null,
    val fathersName: String? = null,
    val mothersName: String? = null,
    val isActive: Boolean? = null
)

@Serializable
data class StudentResponse(
    val pid: String,
    val firstName: String,
    val lastName: String,
    val yearOfBirth: Int?,
    val gender: String,
    val profilePic: String?,
    val schoolClass: String?,
    val villageId: Long,
    val villageName: String,
    val groupId: Long,
    val groupName: String,
    val primaryContactNo: String?,
    val secondaryContactNo: String?,
    val fathersName: String?,
    val mothersName: String?,
    val isActive: Boolean
)

@Serializable
data class StudentListResponse(
    val students: List<StudentResponse>
)

@Serializable
data class StudentGroupHistoryResponse(
    val id: Long,
    val oldGroupId: Long?,
    val oldGroupName: String?,
    val newGroupId: Long,
    val newGroupName: String,
    val assignedByPid: String,
    val assignedAt: String
)

@Serializable
data class StudentGroupHistoryListResponse(
    val history: List<StudentGroupHistoryResponse>
)
