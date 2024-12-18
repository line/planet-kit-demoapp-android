package com.linecorp.planetkit.demo.uikit.usecases

import com.linecorp.planetkit.audio.PlanetKitAudioDescription
import com.linecorp.planetkit.demo.uikit.consts.StringSet
import com.linecorp.planetkit.demo.uikit.repositories.PlanetKitOneOnOneCallRepository
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitConvertUtil
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitVideoStatus
import com.linecorp.planetkit.session.PlanetKitMyMediaStatus
import com.linecorp.planetkit.session.PlanetKitMyMediaStatusListener
import com.linecorp.planetkit.ui.PlanetKitVideoView
import com.linecorp.planetkit.video.PlanetKitScreenShareState
import com.linecorp.planetkit.video.PlanetKitVideoStatus

class OneOnOneCallMyStatusUseCase(
    private val callRepository: PlanetKitOneOnOneCallRepository
): UseCase {
    private val myMediaStatus: PlanetKitMyMediaStatus?
        get() = callRepository.getMyMediaStatus()

    private val kitListener = object: PlanetKitMyMediaStatusListener {
        override fun onMyAudioDescriptionUpdated(audioDescription: PlanetKitAudioDescription) {
        }

        override fun onMyAudioMuted() {
            listener?.onMuteStateChanged(true)
        }

        override fun onMyAudioUnmuted() {
            listener?.onMuteStateChanged(false)
        }

        override fun onScreenShareStateUpdated(state: PlanetKitScreenShareState) {
        }

        override fun onVideoStatusUpdated(videoStatus: PlanetKitVideoStatus) {
            listener?.onVideoStatusUpdated(UIKitConvertUtil.convertVideoStatus(videoStatus))
        }

    }


    interface Listener {
        fun onMuteStateChanged(on: Boolean)
        fun onVideoStatusUpdated(videoStatus: UIKitVideoStatus)
    }

    init {
        myMediaStatus?.addHandler(kitListener, null) {

        }
    }

    private var listener: Listener? = null
    fun setListener(listener: Listener?) {
        this.listener = listener
        myMediaStatus?.let {
            listener?.onMuteStateChanged(it.isMyAudioMuted)
            listener?.onVideoStatusUpdated(UIKitConvertUtil.convertVideoStatus(it.myVideoStatus))
        }
    }

    fun addMyVideoView(videoView: PlanetKitVideoView) {
        callRepository.addMyVideoView(videoView)
        videoView.setOnRenderFirstFrameListener {
        }
    }

    fun removeVideoView(videoView: PlanetKitVideoView) {
        callRepository.removeMyVideoView(videoView)
    }

    fun muteOn(on: Boolean) {
        callRepository.muteOn(on)
    }

    fun pauseVideo() {
        callRepository.pauseMyVideo()
    }

    fun resumeVideo() {
        callRepository.resumeMyVideo()
    }

    override fun onCleared() {
        listener = null
        myMediaStatus?.removeHandler(kitListener, null) {
        }
    }
}