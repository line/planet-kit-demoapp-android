package com.linecorp.planetkit.demo.uikit.repositories.datasources

import com.linecorp.planetkit.demo.uikit.repositories.models.AccessTokenResponse
import com.linecorp.planetkit.demo.uikit.repositories.models.LongPollingResponse
import com.linecorp.planetkit.demo.uikit.repositories.models.RegisterDeviceBody
import com.linecorp.planetkit.demo.uikit.repositories.models.RegisterUserBody
import com.linecorp.planetkit.demo.uikit.repositories.models.TimestampResponse
import com.linecorp.planetkit.demo.uikit.repositories.models.RegisterUserResponse
import com.linecorp.planetkit.demo.uikit.repositories.models.UpdateNotificationTokenBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AppServerAPIs {

    @POST("v2/register_user")
    suspend fun registerUserV2(
        @Body registerUserBody: RegisterUserBody
    ): RegisterUserResponse

    @POST("v2/register_device")
    suspend fun registerDeviceV2(
        @Header("Authorization") authorization: String,
        @Body registerDeviceBody: RegisterDeviceBody
    ): TimestampResponse

    @POST("v2/update_notification_token")
    suspend fun updateNotificationTokenV2(
        @Header("Authorization") authorization: String,
        @Body updateNotificationTokenBody: UpdateNotificationTokenBody
    ): TimestampResponse

    @GET("v2/access_token/issue")
    suspend fun accessTokenV2(
        @Header("Authorization") authorization: String
    ): AccessTokenResponse

    @GET("v2/notification/lp")
    suspend fun longPollingNotification(
        @Header("Authorization") authorization: String
    ) : LongPollingResponse
}