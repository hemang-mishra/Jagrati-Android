package com.hexagraph.jagrati_android.model.user

import com.hexagraph.jagrati_android.model.Gender
import com.hexagraph.jagrati_android.model.Student
import com.google.gson.Gson
import com.google.mlkit.vision.face.FaceLandmark
import com.hexagraph.jagrati_android.model.FaceInfo
import com.hexagraph.jagrati_android.model.ImageKitResponse
import kotlinx.serialization.Serializable

@Serializable
data class StudentDTO(
    val pid: String,
    val firstName: String,
    val lastName: String,
    val yearOfBirth: Int?,
    val gender: Gender,
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
data class VolunteerDTO(
    val pid: String,
    val rollNumber: String?,
    val firstName: String,
    val lastName: String,
    val gender: Gender,
    val alternateEmail: String?,
    val batch: String?,
    val programme: String?,
    val streetAddress1: String?,
    val streetAddress2: String?,
    val pincode: String?,
    val city: String?,
    val state: String?,
    val dateOfBirth: String,
    val contactNumber: String?,
    val college: String?,
    val branch: String?,
    val yearOfStudy: Int?,
    val profilePic: ImageKitResponse? = null,
    val isActive: Boolean
)

@Serializable
data class VillageDTO(
    val id: Long,
    val name: String,
    val isActive: Boolean
)

@Serializable
data class GroupDTO(
    val id: Long,
    val name: String,
    val description: String?,
    val isActive: Boolean
)


fun StudentDTO.toEntity(): Student = Student(
    pid = pid,
    firstName = firstName,
    lastName = lastName,
    yearOfBirth = yearOfBirth,
    gender = gender.name,
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

