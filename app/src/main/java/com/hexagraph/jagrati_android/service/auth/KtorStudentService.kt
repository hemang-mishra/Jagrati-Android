package com.hexagraph.jagrati_android.service.auth

import com.hexagraph.jagrati_android.model.student.StudentGroupHistoryListResponse
import com.hexagraph.jagrati_android.model.student.StudentListResponse
import com.hexagraph.jagrati_android.model.student.StudentRequest
import com.hexagraph.jagrati_android.model.student.StudentResponse
import com.hexagraph.jagrati_android.model.student.UpdateStudentRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class KtorStudentService(
    private val client: HttpClient,
    private val baseUrl: String
) {
    suspend fun registerStudent(request: StudentRequest): String {
        return client.post("$baseUrl/api/students/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun updateStudent(request: UpdateStudentRequest): String {
        return client.put("$baseUrl/api/students/update") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getGroupHistory(pid: String): StudentGroupHistoryListResponse {
        return client.get("$baseUrl/api/students/$pid/group-history").body()
    }

    suspend fun getAllStudents(): StudentListResponse {
        return client.get("$baseUrl/api/students").body()
    }

    suspend fun getStudentByPid(pid: String): StudentResponse {
        return client.get("$baseUrl/api/students/$pid").body()
    }
}

