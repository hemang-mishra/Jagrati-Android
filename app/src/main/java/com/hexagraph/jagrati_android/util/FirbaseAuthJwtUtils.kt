package com.hexagraph.jagrati_android.util

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonObject

/**
 * Utility object for JWT token operations
 */
object FirbaseAuthJwtUtils {

    /**
     * Decodes a JWT token and extracts user information
     */
    fun decodeJwtToken(token: String): JwtPayload? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null

            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP))

            val gson = Gson()
            val jsonObject = gson.fromJson(payload, JsonObject::class.java)

            JwtPayload(
                sub = jsonObject.get("sub")?.asString,
                email = jsonObject.get("email")?.asString,
                name = jsonObject.get("name")?.asString,
                givenName = jsonObject.get("given_name")?.asString,
                familyName = jsonObject.get("family_name")?.asString,
                picture = jsonObject.get("picture")?.asString,
                emailVerified = jsonObject.get("email_verified")?.asBoolean ?: false
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    data class JwtPayload(
        val sub: String?,
        val email: String?,
        val name: String?,
        val givenName: String?,
        val familyName: String?,
        val picture: String?,
        val emailVerified: Boolean
    )
}

