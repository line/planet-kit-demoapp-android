package com.linecorp.planetkit.demo.uikit

import android.content.Context
import android.util.Log
import com.linecorp.planetkit.demo.uikit.consts.StringSet
import com.linecorp.planetkit.demo.uikit.repositories.AppServerRepository
import com.linecorp.planetkit.demo.uikit.repositories.PlanetKitGroupCallRepository
import com.linecorp.planetkit.demo.uikit.repositories.PlanetKitOneOnOneCallRepositoryContainer
import com.linecorp.planetkit.demo.uikit.repositories.PlanetKitRepository
import com.linecorp.planetkit.demo.uikit.repositories.ServiceLocatorRepository

class UikitAppContainer(
    context: Context,
    appServerUrl: String,
    serviceId: String,
    region: String,
    apiKey: String,
    endToneResId: Int? = null,
    holdToneResId: Int? = null,
    ringBackToneResId: Int? = null,
    ringToneResId: Int? = null,
) {
    var kitRepository = PlanetKitRepository()
    var groupCallRepository: PlanetKitGroupCallRepository = PlanetKitGroupCallRepository(context)
    val callRepositoryContainer = PlanetKitOneOnOneCallRepositoryContainer(context)
    val serviceLocatorRepository = ServiceLocatorRepository(context, serviceId, endToneResId, holdToneResId, ringBackToneResId, ringToneResId)
    val appServerRepository = AppServerRepository(context, appServerUrl, serviceId, region, apiKey)

    fun initialize(context: Context, cloudUrl: String, enableLog: Boolean) {
        kitRepository.initialize(context, cloudUrl, enableLog) { isSuccessful, isVideoHwCodecSupport, userAgent ->
            Log.i(TAG, "PlanetKit initialization(isSuccessful=$isSuccessful, " +
                    "isVideoHwCodecSupport=$isVideoHwCodecSupport, userAgent=$userAgent)")
        }
    }

    companion object {
        private const val TAG = "${StringSet.LOG_UIKIT}UikitAppContainer"
    }
}