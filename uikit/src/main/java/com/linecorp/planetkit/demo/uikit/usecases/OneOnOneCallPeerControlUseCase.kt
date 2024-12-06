package com.linecorp.planetkit.demo.uikit.usecases

import android.util.Log
import com.linecorp.planetkit.demo.uikit.consts.StringSet
import com.linecorp.planetkit.demo.uikit.model.OneOnOneCallPeerStateModel
import com.linecorp.planetkit.demo.uikit.repositories.PlanetKitOneOnOneCallRepository
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitVideoStatus
import com.linecorp.planetkit.ui.PlanetKitVideoView

class OneOnOneCallPeerControlUseCase(
    private val callRepository: PlanetKitOneOnOneCallRepository
): UseCase {
    interface Listener {
        fun onMuteChanged(muteOn: Boolean)
        fun onVideoStatusChanged(videoStatus: UIKitVideoStatus)
        fun onAverageVolumeLevelChanged(volume: Int)
    }

    private val listener = object: OneOnOneCallPeerStateModel.Listener {
        override fun onMuteChanged(muteOn: Boolean) {
            kitListener?.onMuteChanged(muteOn)
        }

        override fun onVideoStatusChanged(videoStatus: UIKitVideoStatus) {
            kitListener?.onVideoStatusChanged(videoStatus)
        }

        override fun onAverageVolumeLevelChanged(volume: Int) {
            kitListener?.onAverageVolumeLevelChanged(volume)
        }
    }

    fun setListener(listener: Listener) {
        kitListener = listener
    }

    private var kitListener: Listener? = null

    fun addPeerVideoView(peerView: PlanetKitVideoView) {
        callRepository.addPeerVideoView(peerView)
    }

    fun removePeerVideoView(peerView: PlanetKitVideoView) {
        callRepository.removePeerVideoView(peerView)
    }

    init {
        callRepository.peerStateModel.addStateListener(listener)
    }

    override fun onCleared() {
        callRepository.peerStateModel.removeStateListener(listener)
        kitListener = null
    }
}