package com.hexagraph.jagrati_android.model.student

import com.hexagraph.jagrati_android.model.ImageKitResponse
import com.hexagraph.jagrati_android.model.Student
import kotlinx.serialization.Serializable

@Serializable
data class StudentRequest(
    val pid: String,
    val firstName: String,
    val lastName: String,
    val yearOfBirth: Int? = null,
    val gender: String,
    val profilePic: ImageKitResponse? = null,
    val schoolClass: String? = null,
    val villageId: Long,
    val groupId: Long,
    val primaryContactNo: String? = null,
    val secondaryContactNo: String? = null,
    val fathersName: String? = null,
    val mothersName: String? = null,
)

@Serializable
data class UpdateStudentRequest(
    val pid: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val yearOfBirth: Int? = null,
    val gender: String? = null,
    val profilePic: ImageKitResponse? = null,
    val schoolClass: String? = null,
    val villageId: Long? = null,
    val groupId: Long? = null,
    val primaryContactNo: String? = null,
    val secondaryContactNo: String? = null,
    val fathersName: String? = null,
    val mothersName: String? = null,
    val isActive: Boolean? = null,
)

@Serializable
data class StudentResponse(
    val pid: String,
    val firstName: String,
    val lastName: String,
    val yearOfBirth: Int?,
    val gender: String,
    val profilePic: ImageKitResponse?,
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

// Student <-> StudentRequest
fun StudentRequest.toStudent(): Student = Student(
    pid = pid,
    firstName = firstName,
    lastName = lastName,
    yearOfBirth = yearOfBirth,
    gender = gender,
    profilePic = profilePic,
    schoolClass = schoolClass,
    villageId = villageId,
    groupId = groupId,
    primaryContactNo = primaryContactNo,
    secondaryContactNo = secondaryContactNo,
    fathersName = fathersName,
    mothersName = mothersName,
    isActive = true, // default as not present in request
    registeredByPid = null
)

fun Student.toStudentRequest(): StudentRequest = StudentRequest(
    pid = pid,
    firstName = firstName,
    lastName = lastName,
    yearOfBirth = yearOfBirth,
    gender = gender,
    profilePic = profilePic,
    schoolClass = schoolClass,
    villageId = villageId,
    groupId = groupId,
    primaryContactNo = primaryContactNo,
    secondaryContactNo = secondaryContactNo,
    fathersName = fathersName,
    mothersName = mothersName
)

// Student <-> UpdateStudentRequest
fun UpdateStudentRequest.toStudent(existing: Student): Student = existing.copy(
    firstName = firstName ?: existing.firstName,
    lastName = lastName ?: existing.lastName,
    yearOfBirth = yearOfBirth ?: existing.yearOfBirth,
    gender = gender ?: existing.gender,
    profilePic = profilePic ?: existing.profilePic,
    schoolClass = schoolClass ?: existing.schoolClass,
    villageId = villageId ?: existing.villageId,
    groupId = groupId ?: existing.groupId,
    primaryContactNo = primaryContactNo ?: existing.primaryContactNo,
    secondaryContactNo = secondaryContactNo ?: existing.secondaryContactNo,
    fathersName = fathersName ?: existing.fathersName,
    mothersName = mothersName ?: existing.mothersName,
    isActive = isActive ?: existing.isActive
)

fun Student.toUpdateStudentRequest(): UpdateStudentRequest = UpdateStudentRequest(
    pid = pid,
    firstName = firstName,
    lastName = lastName,
    yearOfBirth = yearOfBirth,
    gender = gender,
    profilePic = profilePic,
    schoolClass = schoolClass,
    villageId = villageId,
    groupId = groupId,
    primaryContactNo = primaryContactNo,
    secondaryContactNo = secondaryContactNo,
    fathersName = fathersName,
    mothersName = mothersName,
    isActive = isActive
)

// Student <-> StudentResponse
fun StudentResponse.toStudent(): Student = Student(
    pid = pid,
    firstName = firstName,
    lastName = lastName,
    yearOfBirth = yearOfBirth,
    gender = gender,
    profilePic = profilePic,
    schoolClass = schoolClass,
    villageId = villageId,
    groupId = groupId,
    primaryContactNo = primaryContactNo,
    secondaryContactNo = secondaryContactNo,
    fathersName = fathersName,
    mothersName = mothersName,
    isActive = isActive,
    registeredByPid = null // Not present in response
)

fun Student.toStudentResponse(
    villageName: String = "",
    groupName: String = ""
): StudentResponse = StudentResponse(
    pid = pid,
    firstName = firstName,
    lastName = lastName,
    yearOfBirth = yearOfBirth,
    gender = gender,
    profilePic = profilePic,
    schoolClass = schoolClass,
    villageId = villageId,
    villageName = villageName,
    groupId = groupId,
    groupName = groupName,
    primaryContactNo = primaryContactNo,
    secondaryContactNo = secondaryContactNo,
    fathersName = fathersName,
    mothersName = mothersName,
    isActive = isActive
)

