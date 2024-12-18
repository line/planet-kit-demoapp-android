package com.linecorp.planetkit.demo.uikit.usecases

import android.content.Context
import com.linecorp.planetkit.PlanetKitMediaType
import com.linecorp.planetkit.PlanetKitStartFailReason
import com.linecorp.planetkit.demo.uikit.model.GroupCallStateModel
import com.linecorp.planetkit.demo.uikit.repositories.PlanetKitGroupCallRepository
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitGroupCallParam
import com.linecorp.planetkit.session.conference.PlanetKitConferenceParam

class GroupCallStateUseCase(
    private val planetKitGroupCallRepository: PlanetKitGroupCallRepository
): UseCase {

    val isConnected
        get() = planetKitGroupCallRepository.stateModel.isConnected

    private val kitListenerList = ArrayList<Listener>()
    private val listener = object :GroupCallStateModel.Listener {
        override fun onConnected(context: Context) {
            kitListenerList.forEach { it.onConnected(context) }
        }

        override fun onDisconnected(context: Context, reason: String) {
            kitListenerList.forEach { it.onDisconnected(context, reason) }
        }
    }

    init {
        planetKitGroupCallRepository.stateModel.addStateListener(listener)
    }

    fun join(param: UIKitGroupCallParam, accessToken: String): PlanetKitStartFailReason {
        val conferenceParam = PlanetKitConferenceParam.Builder()
            .myId(param.myId)
            .myDisplayName(param.myDisplayName)
            .myServiceId(param.myServiceId)
            .mediaType(PlanetKitMediaType.AUDIOVIDEO)
            .roomId(param.roomId)
            .roomServiceId(param.roomServiceId)
            .accessToken(accessToken)
            .build()

        val reason = planetKitGroupCallRepository.join(conferenceParam)
        if (reason == PlanetKitStartFailReason.NONE) {
            if (param.muteOnStart == true) {
                planetKitGroupCallRepository.muteOn(true)
            }
            if (!param.isVideo) {
                planetKitGroupCallRepository.pauseMyVideo()
            }
        }
        return reason
    }

    fun leave() {
        planetKitGroupCallRepository.leave()
    }

    fun addStateListener(listener: Listener) {
        kitListenerList.add(listener)
    }

    fun removeStateListener(listener: Listener) {
        kitListenerList.remove(listener)
    }

    interface Listener {
        fun onConnected(context: Context)
        fun onDisconnected(context: Context, reason: String)
    }

    override fun onCleared() {
        kitListenerList.clear()
        planetKitGroupCallRepository.stateModel.removeStateListener(listener)
    }
}