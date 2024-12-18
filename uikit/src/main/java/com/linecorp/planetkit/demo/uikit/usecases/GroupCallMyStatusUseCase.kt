package com.linecorp.planetkit.demo.uikit.usecases

import com.linecorp.planetkit.audio.PlanetKitAudioDescription
import com.linecorp.planetkit.demo.uikit.repositories.PlanetKitGroupCallRepository
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitConvertUtil
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitUser
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitVideoStatus
import com.linecorp.planetkit.session.PlanetKitMyMediaStatus
import com.linecorp.planetkit.session.PlanetKitMyMediaStatusListener
import com.linecorp.planetkit.ui.PlanetKitVideoView
import com.linecorp.planetkit.video.PlanetKitScreenShareState
import com.linecorp.planetkit.video.PlanetKitVideoStatus

class GroupCallMyStatusUseCase(
    private val planetKitConferenceRepository: PlanetKitGroupCallRepository
): UseCase {

    private val myMediaStatus: PlanetKitMyMediaStatus?
        get() = planetKitConferenceRepository.getMyMediaStatus()

    val isMuteOn: Boolean
        get() = myMediaStatus?.isMyAudioMuted ?: false

    private val kitListener = object: PlanetKitMyMediaStatusListener {
        override fun onMyAudioDescriptionUpdated(audioDescription: PlanetKitAudioDescription) {
            listener?.onAverageAudioVolumeLevel(audioDescription.averageVolumeLevel)
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

    private var listener: Listener? = null

    val me: UIKitUser?
        get() = planetKitConferenceRepository.me?.let { UIKitUser(it.userId, it.serviceId, planetKitConferenceRepository.myDisplayName) }



    init {
        myMediaStatus?.addHandler(kitListener, null) {

        }
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
        myMediaStatus?.let {
            listener?.onMuteStateChanged(it.isMyAudioMuted)
            listener?.onVideoStatusUpdated(UIKitConvertUtil.convertVideoStatus(it.myVideoStatus))
        }
    }

    fun addVideoView(videoView: PlanetKitVideoView) {
        planetKitConferenceRepository.addMyVideo(videoView)
    }

    fun removeVideoView(videoView: PlanetKitVideoView) {
        planetKitConferenceRepository.removeMyVideo(videoView)
    }

    fun muteOn(on: Boolean) {
        planetKitConferenceRepository.muteOn(on)
    }

    fun pauseVideo() {
        planetKitConferenceRepository.pauseMyVideo()
    }

    fun resumeVideo() {
        planetKitConferenceRepository.resumeMyVideo()
    }

    interface Listener {
        fun onMuteStateChanged(on: Boolean)
        fun onVideoStatusUpdated(videoStatus: UIKitVideoStatus)

        fun onAverageAudioVolumeLevel(averageVolumeLevel: Int)
    }

    override fun onCleared() {
        myMediaStatus?.removeHandler(kitListener, null) {

        }
        listener = null
    }
}