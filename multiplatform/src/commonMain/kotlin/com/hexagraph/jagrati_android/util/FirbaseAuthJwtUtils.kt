package com.hexagraph.jagrati_android.util

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Utility object for JWT token operations
 */
object FirbaseAuthJwtUtils {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    /**
     * Decodes a JWT token and extracts user information
     */
    @OptIn(ExperimentalEncodingApi::class)
    fun decodeJwtToken(token: String): JwtPayload? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null
            val payload = Base64.UrlSafe.decode(parts[1]).decodeToString()
//            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP))
            json.decodeFromString(payload)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    @Serializable
    data class JwtPayload(
        val sub: String?,
        val email: String?,
        val name: String?,
        @SerialName("given_name")
        val givenName: String?,
        @SerialName("family_name")
        val familyName: String?,
        val picture: String?,
        @SerialName("email_verified")
        val emailVerified: Boolean
    )
}