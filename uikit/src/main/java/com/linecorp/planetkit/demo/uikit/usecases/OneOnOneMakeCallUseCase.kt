package com.linecorp.planetkit.demo.uikit.usecases

import android.util.Log
import com.linecorp.planetkit.PlanetKitCallResult
import com.linecorp.planetkit.PlanetKitMediaType
import com.linecorp.planetkit.PlanetKitStartFailReason
import com.linecorp.planetkit.demo.uikit.consts.StringSet
import com.linecorp.planetkit.demo.uikit.repositories.PlanetKitOneOnOneCallRepository
import com.linecorp.planetkit.demo.uikit.repositories.PlanetKitOneOnOneCallRepositoryContainer
import com.linecorp.planetkit.demo.uikit.repositories.ServiceLocatorRepository
import com.linecorp.planetkit.session.call.PlanetKitMakeCallParam

class OneOnOneMakeCallUseCase(
    private val serviceLocatorRepository: ServiceLocatorRepository,
    private val callRepositoryContainer: PlanetKitOneOnOneCallRepositoryContainer
) {
    val myId = serviceLocatorRepository.userId

    fun makeCall(
        peerId: String, isVideoCall: Boolean, accessToken: String,
    ): PlanetKitCallResult {
        val ringBackTone = serviceLocatorRepository.ringBackToneUri
        val holdTone = serviceLocatorRepository.holdToneUri
        val endTone = serviceLocatorRepository.endToneUri

        val paramBuilder = PlanetKitMakeCallParam.Builder()
            .myId(serviceLocatorRepository.userId)
            .myServiceId(serviceLocatorRepository.serviceId)
            .peerId(peerId)
            .peerServiceId(serviceLocatorRepository.serviceId)
            .accessToken(accessToken)
            .mediaType(
                if (!isVideoCall) PlanetKitMediaType.AUDIO else PlanetKitMediaType.AUDIOVIDEO
            )

        if (endTone != null) paramBuilder.endTone(endTone)
        if (holdTone != null) paramBuilder.holdTone(holdTone)
        if (ringBackTone != null) paramBuilder.ringbackTone(ringBackTone)

        val callRepository = PlanetKitOneOnOneCallRepository(callRepositoryContainer)
        return callRepository.makeCall(paramBuilder.build())
    }

    companion object {
        private const val TAG = "${StringSet.LOG_UIKIT}OneOnOneMakeCallUseCase"
    }
}