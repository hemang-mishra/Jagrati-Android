package com.hexagraph.jagrati_android.service.auth

import com.hexagraph.jagrati_android.model.group.GroupListResponse
import com.hexagraph.jagrati_android.model.village.LongRequest
import com.hexagraph.jagrati_android.model.village.NameDescriptionRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class KtorGroupService(
    private val client: HttpClient,
    private val baseUrl: String
) {
    suspend fun addGroup(request: NameDescriptionRequest): String {
        return client.post("$baseUrl/api/groups/add") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun removeGroup(request: LongRequest): String {
        return client.delete("$baseUrl/api/groups/remove") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getAllActiveGroups(): GroupListResponse {
        return client.get("$baseUrl/api/groups").body()
    }
}

