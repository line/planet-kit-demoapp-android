package com.linecorp.planetkit.demo.uikit.repositories

import android.content.Context
import android.util.Log
import com.linecorp.planetkit.PlanetKit
import com.linecorp.planetkit.PlanetKitStartFailReason
import com.linecorp.planetkit.PlanetKitVideoPauseReason
import com.linecorp.planetkit.demo.uikit.model.GroupCallPeerListModel
import com.linecorp.planetkit.demo.uikit.model.GroupCallStateModel
import com.linecorp.planetkit.session.PlanetKitDisconnectedParam
import com.linecorp.planetkit.session.PlanetKitMyMediaStatus
import com.linecorp.planetkit.session.PlanetKitUser
import com.linecorp.planetkit.session.conference.ConferenceListener
import com.linecorp.planetkit.session.conference.PlanetKitConference
import com.linecorp.planetkit.session.conference.PlanetKitConferenceParam
import com.linecorp.planetkit.session.conference.PlanetKitConferencePeerListUpdatedParam
import com.linecorp.planetkit.session.conference.PlanetKitConferenceVideoUpdateParam
import com.linecorp.planetkit.ui.PlanetKitVideoView

class PlanetKitGroupCallRepository(private val context: Context) {
    private var conference: PlanetKitConference? = null

    val peerListModel = GroupCallPeerListModel()
    val stateModel = GroupCallStateModel()

    val roomName: String?
        get() = conference?.roomId
    val durationMilliseconds: Int?
        get() = conference?.duration
    val me: PlanetKitUser?
        get() = conference?.me

    val myDisplayName: String?
        get() = conference?.myDisplayName

    private val conferenceListener = object: ConferenceListener {
        override fun onConnected(
            conference: PlanetKitConference,
            isVideoHwCodecEnabled: Boolean,
            isVideoShareModeSupported: Boolean
        ) {
            stateModel.update(context, conference.state)
        }

        override fun onDisconnected(
            conference: PlanetKitConference,
            param: PlanetKitDisconnectedParam
        ) {
            super.onDisconnected(conference, param)
            stateModel.update(context, conference.state, param.reason.name)
            peerListModel.clear()
        }

        override fun onPeersVideoUpdated(
            conference: PlanetKitConference,
            param: PlanetKitConferenceVideoUpdateParam
        ) {
        }

        override fun onPeerListUpdated(param: PlanetKitConferencePeerListUpdatedParam) {
            peerListModel.update(param)
        }
    }

    fun join(param: PlanetKitConferenceParam): PlanetKitStartFailReason
    {
        val result = PlanetKit.joinConference(param, conferenceListener)
        conference = result.conference
        return result.reason
    }

    fun leave() {
        conference?.leaveConference()
        conference = null
    }

    fun getMyMediaStatus(): PlanetKitMyMediaStatus? {
        return conference?.getMyMediaStatus()
    }

    fun addPeerVideo(peer: PlanetKitUser, planetKitVideoView: PlanetKitVideoView) {
        conference?.addPeerVideoView(peer, planetKitVideoView)
    }

    fun removePeerVideo(peer: PlanetKitUser, planetKitVideoView: PlanetKitVideoView) {
        conference?.removePeerVideoView(peer, planetKitVideoView)
    }

    fun addMyVideo(planetKitVideoView: PlanetKitVideoView) {
        conference?.addMyVideoView(planetKitVideoView)
    }

    fun removeMyVideo(planetKitVideoView: PlanetKitVideoView) {
        conference?.removePeerVideoView(planetKitVideoView)
    }

    fun muteOn(on: Boolean) {
        conference?.muteMyAudio(on)
    }

    fun pauseMyVideo() {
        conference?.pauseMyVideo(PlanetKitVideoPauseReason.UNDEFINED)
    }

    fun resumeMyVideo() {
        conference?.resumeMyVideo()
    }
}