package com.hexagraph.jagrati_android.repository.student

import com.hexagraph.jagrati_android.model.StudentDetails

interface AddStudentRepository {
    suspend fun upsertStudent(studentDetails: StudentDetails)

    suspend fun checkIfStudentWithPIDExists(pid: String): Boolean

    suspend fun getStudentDetails(pid: String): StudentDetails?
}