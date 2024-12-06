package com.linecorp.planetkit.demo.uikit.repositories

import android.content.Context
import com.linecorp.planetkit.PlanetKit
import com.linecorp.planetkit.demo.uikit.consts.StringSet
import com.linecorp.planetkit.demo.uikit.repositories.datasources.AppServerDataSource
import com.linecorp.planetkit.demo.uikit.repositories.models.LongPollingResult

class AppServerRepository(context: Context, appServerUrl: String, private val serviceId: String,
                          private val region: String, private val apiKey: String) {

    private val preference: Preference by lazy {
        Preference(context)
    }

    private val appServerDataSource: AppServerDataSource by lazy {
        AppServerDataSource(appServerUrl)
    }

    suspend fun registerUserV2(userId: String, displayName: String?): String? {
        appServerDataSource.registerUserV2(apiKey, userId, serviceId, region, displayName)?.let {
            return it
        }
        return null
    }

    suspend fun registerDeviceV2(appServerAuth: String) {
        appServerDataSource.registerDeviceV2(APP_SERVER_AUTH_BEARER + appServerAuth, PlanetKit.version)
    }

    suspend fun updateNotificationTokenV2(appServerAuth: String, notificationType: String, newToken: String, apnsServer: String?, newRegister: Boolean) {
        if (newToken == latestUpdatedToken && !newRegister) {
            return
        }
        appServerDataSource.updateNotificationTokenV2(APP_SERVER_AUTH_BEARER + appServerAuth, PlanetKit.version, notificationType, newToken, apnsServer)
        latestUpdatedToken = newToken
    }

    suspend fun getAccessTokenV2(appServerAuth: String): String? {
        return appServerDataSource.getAccessTokenV2(APP_SERVER_AUTH_BEARER + appServerAuth)
    }

    suspend fun longPollingNotification(appServerAuth: String): LongPollingResult {
        val response = appServerDataSource.longPollingNotification(APP_SERVER_AUTH_BEARER + appServerAuth)
        return LongPollingResult(
            response.data?.cc_param,
            response.errorCode?: -1
        )
    }

    private var latestUpdatedToken: String
        set(value) = preference.putString(Preference.KEY_LATEST_UPDATED_TOKEN, value)
        get() = preference.getString(Preference.KEY_LATEST_UPDATED_TOKEN, "").toString()

    companion object {
        private const val TAG = "${StringSet.LOG_UIKIT}AppServerRepository"
        const val APP_SERVER_AUTH_BEARER = "Bearer "
        const val EXPIRE_TIME_KEY = "exp"
    }
}