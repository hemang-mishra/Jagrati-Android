package com.hexagraph.jagrati_android.util

import com.hexagraph.jagrati_android.model.Student
import com.hexagraph.jagrati_android.model.student.StudentRequest
import com.hexagraph.jagrati_android.model.student.StudentResponse
import com.hexagraph.jagrati_android.model.student.UpdateStudentRequest
import com.hexagraph.jagrati_android.model.user.StudentDTO

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