package com.linecorp.planetkit.demo.uikit.usecases

import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import com.linecorp.planetkit.PlanetKitStartFailReason
import com.linecorp.planetkit.demo.uikit.consts.StringSet
import com.linecorp.planetkit.demo.uikit.model.OneOnOneVerifyCallback
import com.linecorp.planetkit.demo.uikit.repositories.PlanetKitOneOnOneCallRepository
import com.linecorp.planetkit.demo.uikit.repositories.PlanetKitOneOnOneCallRepositoryContainer
import com.linecorp.planetkit.demo.uikit.repositories.PlanetKitRepository
import com.linecorp.planetkit.demo.uikit.repositories.ServiceLocatorRepository

class OneOnOneVerifyCallUseCase(private val kitRepository: PlanetKitRepository,
                                private val serviceLocatorRepository: ServiceLocatorRepository,
                                private val callRepositoryContainer: PlanetKitOneOnOneCallRepositoryContainer) {
    val isPlanetKitInitialized: Boolean
        get() = kitRepository.isInitialized

    fun verifyCall(
        message: RemoteMessage,
        callback: OneOnOneVerifyCallback
    ) {
        val cCParamStr = message.data["cc_param"]
        cCParamStr?.let {
            verifyCall(it, callback)
        }
        return
    }

    fun verifyCall(
        cCParamStr: String,
        callback: OneOnOneVerifyCallback
    ) {
        val callRepository = PlanetKitOneOnOneCallRepository(callRepositoryContainer)
        val myUserId = serviceLocatorRepository.userId
        val myServiceId = serviceLocatorRepository.serviceId

        if (myUserId.isEmpty() || myServiceId.isEmpty()) {
            return
        }

        val holdToneUri = serviceLocatorRepository.holdToneUri
        val ringToneUri = serviceLocatorRepository.ringToneUri
        val endToneUri = serviceLocatorRepository.endToneUri

        val result = callRepository.verifyCall(myUserId, myServiceId, cCParamStr, holdToneUri, ringToneUri, endToneUri, callback)
        if (result?.reason == PlanetKitStartFailReason.NONE) {
            Log.i(TAG, "Success verify call: ${result?.call!!.instanceId}")
        }
        else {
            Log.e(TAG, "Failed verify call: ${result?.reason}")
        }
    }

    companion object {
        private const val TAG = "${StringSet.LOG_UIKIT}VerifyOneOnOneCallUseCase"
    }
}