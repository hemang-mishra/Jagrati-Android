package com.hexagraph.jagrati_android.model

import kotlinx.serialization.Serializable


@Serializable
data class ImageKitResponse(
    val fileId: String = "",
    val name: String = "",
    val url: String = "",
    val thumbnailUrl: String? = null,
    val height: Int? = null,
    val width: Int? = null,
    val size: Long? = null,
    val filePath: String? = null,
)

@Serializable
data class ImageKitCredentials(
    val token: String,
    val expire: Long,
    val signature: String
)