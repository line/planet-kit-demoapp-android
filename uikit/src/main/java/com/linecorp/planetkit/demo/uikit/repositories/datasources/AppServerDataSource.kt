package com.linecorp.planetkit.demo.uikit.repositories.datasources

import com.linecorp.planetkit.demo.uikit.repositories.models.LongPollingResponse
import com.linecorp.planetkit.demo.uikit.repositories.models.RegisterDeviceBody
import com.linecorp.planetkit.demo.uikit.repositories.models.RegisterUserBody
import com.linecorp.planetkit.demo.uikit.repositories.models.UpdateNotificationTokenBody
import com.linecorp.planetkit.demo.uikit.repositories.models.checkError
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppServerDataSource(appServerUrl: String) {

    private var appServerAPIs: AppServerAPIs
    private var longPollingAPI: AppServerAPIs
    private var retrofit: Retrofit
    private var longPollingRetrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(appServerUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
        appServerAPIs = retrofit.create(AppServerAPIs::class.java)

        longPollingRetrofit = Retrofit.Builder()
            .baseUrl(appServerUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(longPollingHttpClient)
            .build()
        longPollingAPI = longPollingRetrofit.create(AppServerAPIs::class.java)
    }

    suspend fun registerUserV2(apiKey: String, userId: String, serviceId: String, region: String, displayName: String?): String? {
        val response = appServerAPIs.registerUserV2(RegisterUserBody(apiKey, userId, serviceId, region, displayName))
        response.checkError()
        return response.data?.accessToken
    }

    suspend fun registerDeviceV2(appServerAccessToken: String, appVersion: String) {
        appServerAPIs.registerDeviceV2(appServerAccessToken, RegisterDeviceBody(APP_TYPE, appVersion)).checkError()
    }

    suspend fun updateNotificationTokenV2(appServerAccessToken: String, appVersion: String, notificationType: String, newToken: String, apnsServer: String?) {
        appServerAPIs.updateNotificationTokenV2(appServerAccessToken, UpdateNotificationTokenBody(
            APP_TYPE,
            appVersion,
            notificationType,
            newToken,
            apnsServer
        )).checkError()
    }

    suspend fun getAccessTokenV2(appServerAccessToken: String): String? {
        val response = appServerAPIs.accessTokenV2(appServerAccessToken)
        response.checkError()
        return response.data?.accessToken
    }

    suspend fun longPollingNotification(appServerAccessToken: String): LongPollingResponse {
        return longPollingAPI.longPollingNotification(appServerAccessToken)
    }

    private val httpClient: OkHttpClient get() {
        return OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            addInterceptor { chain ->
                val original = chain.request()
                val request: Request = original.newBuilder()
                    .header("Accept", "application/json")
                    .method(original.method(), original.body())
                    .build()
                chain.proceed(request)
            }
        }.build()
    }

    private val longPollingHttpClient: OkHttpClient get() {
        return OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            addInterceptor { chain ->
                val original = chain.request()
                val request: Request = original.newBuilder()
                    .header("Accept", "application/json")
                    .method(original.method(), original.body())
                    .build()
                chain.proceed(request)
            }
        }.connectTimeout(240, TimeUnit.SECONDS)
        .readTimeout(240, TimeUnit.SECONDS)
        .writeTimeout(240, TimeUnit.SECONDS)
        .build()
    }

    companion object {
        private const val APP_TYPE = "ANDROID"
    }
}