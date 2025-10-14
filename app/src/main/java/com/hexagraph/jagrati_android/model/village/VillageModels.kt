package com.hexagraph.jagrati_android.model.village

import kotlinx.serialization.Serializable

@Serializable
data class StringRequest(
    val value: String
)

@Serializable
data class LongRequest(
    val value: Long
)

@Serializable
data class NameDescriptionRequest(
    val name: String,
    val description: String
)

@Serializable
data class StringResponse(
    val message: String
)

@Serializable
data class LongStringResponse(
    val data: String,
    val id: Long
)

@Serializable
data class VillageListResponse(
    val villages: List<LongStringResponse>
)

