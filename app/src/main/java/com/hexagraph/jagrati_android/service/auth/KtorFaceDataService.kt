package com.hexagraph.jagrati_android.service.auth

import com.hexagraph.jagrati_android.model.facedata.AddFaceDataRequest
import com.hexagraph.jagrati_android.model.facedata.FaceDataResponse
import com.hexagraph.jagrati_android.model.facedata.StudentWithFaceDataListResponse
import com.hexagraph.jagrati_android.model.facedata.UpdateFaceDataRequest
import com.hexagraph.jagrati_android.model.facedata.VolunteerWithFaceDataListResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class KtorFaceDataService(
    private val client: HttpClient,
    private val baseUrl: String
) {
    suspend fun addFaceData(request: AddFaceDataRequest): FaceDataResponse {
        return client.post("$baseUrl/api/face-data") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun updateFaceData(pid: String, request: UpdateFaceDataRequest): FaceDataResponse {
        return client.put("$baseUrl/api/face-data/$pid") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun deleteFaceData(pid: String) {
        client.delete("$baseUrl/api/face-data/$pid")
    }

    suspend fun getFaceDataByPid(pid: String): FaceDataResponse {
        return client.get("$baseUrl/api/face-data/$pid").body()
    }

    suspend fun getAllStudentsWithFaceData(): StudentWithFaceDataListResponse {
        return client.get("$baseUrl/api/face-data/students").body()
    }

    suspend fun getAllVolunteersWithFaceData(): VolunteerWithFaceDataListResponse {
        return client.get("$baseUrl/api/face-data/volunteers").body()
    }

    suspend fun addFaceDataForMe(request: UpdateFaceDataRequest): FaceDataResponse {
        return client.post("$baseUrl/api/face-data/me") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getMyFaceData(): FaceDataResponse {
        return client.get("$baseUrl/api/face-data/me").body()
    }
}

