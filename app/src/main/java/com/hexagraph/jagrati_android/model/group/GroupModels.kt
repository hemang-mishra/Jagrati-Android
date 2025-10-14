package com.hexagraph.jagrati_android.model.group

import kotlinx.serialization.Serializable

@Serializable
data class GroupListResponse(
    val groups: List<GroupResponse>
)

@Serializable
data class GroupResponse(
    val data: String,
    val id: Long
)

