package com.hexagraph.jagrati_android.model.user

import com.hexagraph.jagrati_android.model.Gender
import com.hexagraph.jagrati_android.model.Student
import com.google.gson.Gson
import com.google.mlkit.vision.face.FaceLandmark
import com.hexagraph.jagrati_android.model.FaceInfo
import kotlinx.serialization.Serializable

@Serializable
data class StudentDTO(
    val pid: String,
    val firstName: String,
    val lastName: String,
    val yearOfBirth: Int?,
    val gender: Gender,
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

@Serializable
data class FaceDataDTO(
    val pid: String,
    val name: String?,
    val faceLink: String?,
    val frameLink: String?,
    val imageLink: String?,
    val width: Int?,
    val height: Int?,
    val faceWidth: Int?,
    val faceHeight: Int?,
    val top: Int?,
    val left: Int?,
    val right: Int?,
    val bottom: Int?,
    val landmarks: String?,
    val smilingProbability: Float?,
    val leftEyeOpenProbability: Float?,
    val rightEyeOpenProbability: Float?,
    val timestamp: String?,
    val time: Long?
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


fun FaceDataDTO.toEntity(): FaceInfo = FaceInfo(
    pid = pid,
    name = name ?: "",
    faceLink = faceLink ?: "",
    frameLink = frameLink ?: "",
    imageLink = imageLink ?: "",
    width = width ?: 0,
    height = height ?: 0,
    faceWidth = faceWidth ?: 0,
    faceHeight = faceHeight ?: 0,
    top = top ?: 0,
    left = left ?: 0,
    right = right ?: 0,
    bottom = bottom ?: 0,
    landmarks = if (!landmarks.isNullOrBlank()) Gson().fromJson(landmarks, object : com.google.gson.reflect.TypeToken<List<FaceLandmark>>() {}.type) else listOf(),
    smilingProbability = smilingProbability ?: 0f,
    leftEyeOpenProbability = leftEyeOpenProbability ?: 0f,
    rightEyeOpenProbability = rightEyeOpenProbability ?: 0f,
    timestamp = timestamp ?: com.hexagraph.jagrati_android.util.Utils.timestamp(),
    time = time ?: System.currentTimeMillis()
)

fun FaceInfo.toDTO(): FaceDataDTO = FaceDataDTO(
    pid = pid,
    name = name,
    faceLink = faceLink,
    frameLink = frameLink,
    imageLink = imageLink,
    width = width,
    height = height,
    faceWidth = faceWidth,
    faceHeight = faceHeight,
    top = top,
    left = left,
    right = right,
    bottom = bottom,
    landmarks = Gson().toJson(landmarks),
    smilingProbability = smilingProbability,
    leftEyeOpenProbability = leftEyeOpenProbability,
    rightEyeOpenProbability = rightEyeOpenProbability,
    timestamp = timestamp,
    time = time
)
