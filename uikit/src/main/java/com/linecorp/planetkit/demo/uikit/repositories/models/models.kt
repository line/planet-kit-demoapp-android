package com.linecorp.planetkit.demo.uikit.repositories.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.Date

class ApiError(val code: Int, override val message: String?) : RuntimeException()

@Keep
open class BaseResponse {
    @SerializedName("status")
    val status: String = "success"

    @SerializedName("code")
    val errorCode: Int? = null

    @SerializedName("message")
    val errorMessage: String? = null
}

fun <T : BaseResponse> T.checkError(): T {
    if (status == "success") {
        return this
    }

    throw ApiError(code = this.errorCode!!, message = this.errorMessage);
}

@Keep
data class RegisterUserBody(
    val apiKey: String,
    val userId: String,
    val serviceId: String,
    val region: String,
    val displayName: String?
)

@Keep
data class RegisterDeviceBody(
    val appType: String,
    val appVer: String
)

@Keep
data class UpdateNotificationTokenBody(
    val appType: String,
    val appVer: String,
    val notificationType: String,
    val notificationToken: String,
    val apnsServer: String?,
)

@Keep
data class LongPollingResponseData(
    val cc_param: String
)

@Keep
class RegisterUserResponse(
    @SerializedName("data")
    val data: AccessTokenData? = null
) : BaseResponse()

@Keep
class TimestampResponse(
    @SerializedName("timestamp")
    val timestamp: String? = null
) : BaseResponse()

@Keep
class AccessTokenData(
    @SerializedName("accessToken")
    val accessToken: String? = null
)

@Keep
class GWAccessTokenData(
    @SerializedName("gwAccessToken")
    val accessToken: String? = null
)

@Keep
class AccessTokenResponse(
    @SerializedName("data")
    val data: GWAccessTokenData? = null
) : BaseResponse()

@Keep
class LongPollingResponse(
    @SerializedName("data")
    val data: LongPollingResponseData? = null,
) : BaseResponse()

data class LongPollingResult(
    val cCParam: String?,
    val code: Int?
)

data class Environment(
    val name: String,
    val appServerUrl: String,
    val saturnUrl: String,
    val serviceId: String,
    val callerId: String,
    val calleeId: String,
    val roomId: String,
    val regionCode: String
)
