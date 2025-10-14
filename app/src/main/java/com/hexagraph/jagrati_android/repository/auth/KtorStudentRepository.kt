package com.hexagraph.jagrati_android.repository.auth

import com.hexagraph.jagrati_android.model.student.StudentGroupHistoryListResponse
import com.hexagraph.jagrati_android.model.student.StudentListResponse
import com.hexagraph.jagrati_android.model.student.StudentRequest
import com.hexagraph.jagrati_android.model.student.StudentResponse
import com.hexagraph.jagrati_android.model.student.UpdateStudentRequest
import com.hexagraph.jagrati_android.service.auth.KtorStudentService
import com.hexagraph.jagrati_android.util.Resource
import com.hexagraph.jagrati_android.util.Utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class KtorStudentRepository(
    private val studentService: KtorStudentService
) : StudentRepository {
    override suspend fun registerStudent(request: StudentRequest): Flow<Resource<String>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { studentService.registerStudent(request) }
        emit(response)
    }

    override suspend fun updateStudent(request: UpdateStudentRequest): Flow<Resource<String>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { studentService.updateStudent(request) }
        emit(response)
    }

    override suspend fun getGroupHistory(pid: String): Flow<Resource<StudentGroupHistoryListResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { studentService.getGroupHistory(pid) }
        emit(response)
    }

    override suspend fun getAllStudents(): Flow<Resource<StudentListResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { studentService.getAllStudents() }
        emit(response)
    }

    override suspend fun getStudentByPid(pid: String): Flow<Resource<StudentResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { studentService.getStudentByPid(pid) }
        emit(response)
    }
}

