package com.linecorp.planetkit.demo.uikit.usecases

import com.linecorp.planetkit.PlanetKitVideoResolution
import com.linecorp.planetkit.audio.PlanetKitAudioDescription
import com.linecorp.planetkit.demo.uikit.repositories.PlanetKitGroupCallRepository
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitConvertUtil
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitUser
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitVideoStatus
import com.linecorp.planetkit.session.conference.PlanetKitPeerControl
import com.linecorp.planetkit.session.conference.subgroup.PlanetKitConferencePeer
import com.linecorp.planetkit.ui.PlanetKitVideoView
import com.linecorp.planetkit.video.PlanetKitVideoStatus

class GroupCallPeerControlUseCase(
    private val planetKitConferenceRepository: PlanetKitGroupCallRepository
): UseCase {

    private var peerControl: PlanetKitPeerControl? = null
    private var videoView: PlanetKitVideoView? = null
    private var listener: Listener? = null

    private val peerControlListener = object: PlanetKitPeerControl.PeerControlListener {
        override fun onMicMuted(peer: PlanetKitConferencePeer) {
            super.onMicMuted(peer)
            listener?.onMuteChanged(true)
        }

        override fun onMicUnmuted(peer: PlanetKitConferencePeer) {
            super.onMicUnmuted(peer)
            listener?.onMuteChanged(false)
        }

        override fun onVideoUpdated(
            peer: PlanetKitConferencePeer,
            videoStatus: PlanetKitVideoStatus,
            subgroupName: String?
        ) {
            super.onVideoUpdated(peer, videoStatus, subgroupName)
            listener?.onVideoStatusChanged(UIKitConvertUtil.convertVideoStatus(videoStatus))
        }

        override fun onAudioDescriptionUpdated(
            peer: PlanetKitConferencePeer,
            audioDescription: PlanetKitAudioDescription
        ) {
            super.onAudioDescriptionUpdated(peer, audioDescription)
            listener?.onAverageVolumeLevelChanged(audioDescription.averageVolumeLevel)
        }
    }

    fun init(user: UIKitUser, videoView: PlanetKitVideoView?, listener: Listener) {
        peerControl?.let {
            this.videoView?.let {v ->
                planetKitConferenceRepository.removePeerVideo(it.peer.user, v)
                this.videoView = null
            }
            it.unregister()
        }
        peerControl = null
        this.listener = listener

        planetKitConferenceRepository.peerListModel.peerList.forEach {
            if (it.user.userId == user.id && it.user.serviceId == user.serviceId) {
                peerControl = it.createPeerControl()
                peerControl?.register(peerControlListener) {result ->
                    if (result.isSuccessful) {
                        videoView?.let {v ->
                            this.videoView = v
                            planetKitConferenceRepository.addPeerVideo(it.user, v)
                        }

                        peerControl?.peer?.let { peer ->
                            listener.onMuteChanged(peer.isAudioMuted)
                            val videoStatusResult = peer.getVideoStatus(null)
                            if (videoStatusResult.failReason == PlanetKitConferencePeer.PeerGetFailReason.NONE) {
                                listener.onVideoStatusChanged(UIKitConvertUtil.convertVideoStatus(videoStatusResult.videoStatus))
                            }
                        }
                    }
                }

                return@forEach
            }
        }
    }

    fun startVideo() {
        peerControl?.startVideo(PlanetKitVideoResolution.RECOMMENDED)
    }

    fun stopVideo() {
        peerControl?.stopVideo()
    }

    interface Listener {
        fun onMuteChanged(muteOn: Boolean)
        fun onVideoStatusChanged(videoStatus: UIKitVideoStatus)
        fun onAverageVolumeLevelChanged(level: Int)
    }

    override fun onCleared() {
        listener = null
        peerControl?.unregister()
    }
}