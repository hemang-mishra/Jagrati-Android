package com.hexagraph.jagrati_android.service.auth

import com.hexagraph.jagrati_android.model.village.LongRequest
import com.hexagraph.jagrati_android.model.village.StringRequest
import com.hexagraph.jagrati_android.model.village.VillageListResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class KtorVillageService(
    private val client: HttpClient,
    private val baseUrl: String
) {
    suspend fun addVillage(village: StringRequest): String {
        return client.post("$baseUrl/api/village/add") {
            contentType(ContentType.Application.Json)
            setBody(village)
        }.body()
    }

    suspend fun removeVillage(villageId: LongRequest): String {
        return client.delete("$baseUrl/api/village/remove") {
            contentType(ContentType.Application.Json)
            setBody(villageId)
        }.body()
    }

    suspend fun getAllActiveVillages(): VillageListResponse {
        return client.get("$baseUrl/api/village").body()
    }
}

