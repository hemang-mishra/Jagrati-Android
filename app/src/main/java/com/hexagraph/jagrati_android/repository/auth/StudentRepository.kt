package com.hexagraph.jagrati_android.repository.auth

import com.hexagraph.jagrati_android.model.student.StudentGroupHistoryListResponse
import com.hexagraph.jagrati_android.model.student.StudentListResponse
import com.hexagraph.jagrati_android.model.student.StudentRequest
import com.hexagraph.jagrati_android.model.student.StudentResponse
import com.hexagraph.jagrati_android.model.student.UpdateStudentRequest
import com.hexagraph.jagrati_android.util.Resource
import kotlinx.coroutines.flow.Flow

interface StudentRepository {
    suspend fun registerStudent(request: StudentRequest): Flow<Resource<String>>

    suspend fun updateStudent(request: UpdateStudentRequest): Flow<Resource<String>>

    suspend fun getGroupHistory(pid: String): Flow<Resource<StudentGroupHistoryListResponse>>

    suspend fun getAllStudents(): Flow<Resource<StudentListResponse>>

    suspend fun getStudentByPid(pid: String): Flow<Resource<StudentResponse>>
}

