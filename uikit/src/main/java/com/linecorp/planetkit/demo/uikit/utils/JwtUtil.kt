package com.linecorp.planetkit.demo.uikit.utils

import android.util.Base64
import org.json.JSONObject

object JwtUtil {

    enum class Type {
        HEADER,
        PAYLOAD,
        SIGNATURE,
    }

    fun decodeJwt(jwt: String): Map<Type, Any> {
        val parts = jwt.split(".")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid JWT token")
        }

        val header = decodeBase64Url(parts[0])
        val payload = decodeBase64Url(parts[1])
        val signature = parts[2]

        return mapOf(
            Type.HEADER to JSONObject(header).toString(4),
            Type.PAYLOAD to JSONObject(payload).toString(4),
            Type.SIGNATURE to signature
        )
    }

    private fun decodeBase64Url(encoded: String): String {
        val decodedBytes = Base64.decode(encoded, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        return String(decodedBytes, Charsets.UTF_8)
    }
}