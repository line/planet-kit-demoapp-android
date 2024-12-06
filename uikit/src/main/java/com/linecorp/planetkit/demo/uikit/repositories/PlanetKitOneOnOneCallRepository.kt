package com.linecorp.planetkit.demo.uikit.repositories

import android.net.Uri
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import com.linecorp.planetkit.PlanetKit
import com.linecorp.planetkit.PlanetKitCallResult
import com.linecorp.planetkit.PlanetKitMediaType
import com.linecorp.planetkit.PlanetKitStartFailReason
import com.linecorp.planetkit.PlanetKitVideoPauseReason
import com.linecorp.planetkit.audio.PlanetKitAudioDescription
import com.linecorp.planetkit.demo.uikit.consts.StringSet
import com.linecorp.planetkit.demo.uikit.model.OneOnOneCallPeerStateModel
import com.linecorp.planetkit.demo.uikit.model.OneOnOneCallStateModel
import com.linecorp.planetkit.demo.uikit.model.OneOnOneVerifyCallback
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitVideoStatus
import com.linecorp.planetkit.session.PlanetKitDisconnectedParam
import com.linecorp.planetkit.session.PlanetKitMediaDisableReason
import com.linecorp.planetkit.session.PlanetKitMyMediaStatus
import com.linecorp.planetkit.session.PlanetKitUser
import com.linecorp.planetkit.session.call.AcceptCallListener
import com.linecorp.planetkit.session.call.MakeCallListener
import com.linecorp.planetkit.session.call.PlanetKitCCParam
import com.linecorp.planetkit.session.call.PlanetKitCall
import com.linecorp.planetkit.session.call.PlanetKitCallConnectedParam
import com.linecorp.planetkit.session.call.PlanetKitCallStartMessage
import com.linecorp.planetkit.session.call.PlanetKitCallState
import com.linecorp.planetkit.session.call.PlanetKitMakeCallParam
import com.linecorp.planetkit.session.call.PlanetKitVerifyCallParam
import com.linecorp.planetkit.session.call.VerifyListener
import com.linecorp.planetkit.ui.PlanetKitVideoView

class PlanetKitOneOnOneCallRepository(
    private val repositoryContainer: PlanetKitOneOnOneCallRepositoryContainer
) {
    private var call: PlanetKitCall? = null

    val callStateModel = OneOnOneCallStateModel()
    val peerStateModel = OneOnOneCallPeerStateModel()

    val mediaType: PlanetKitMediaType?
        get() = call?.mediaType


    val peerId: String?
        get() = call?.peer?.userId
    val me: PlanetKitUser?
        get() = call?.me

    val durationMilliseconds: Int?
        get() = call?.duration

    val peerCallStartMessage: PlanetKitCallStartMessage?
        get() = call?.peerCallStartMessage

    val peerUseResponderPreparation: Boolean
        get() = call?.peerUseResponderPreparation == true

    fun getMyMediaStatus(): PlanetKitMyMediaStatus? {
        return call?.getMyMediaStatus()
    }

    fun addMyVideoView(myVideoView: PlanetKitVideoView) {
        call?.addMyVideoView(myVideoView)
    }

    fun removeMyVideoView(myVideoView: PlanetKitVideoView) {
        call?.removeMyVideoView(myVideoView)
    }

    fun addPeerVideoView(peerVideoView: PlanetKitVideoView) {
        call?.addPeerVideoView(peerVideoView)
    }

    fun removePeerVideoView(peerVideoView: PlanetKitVideoView) {
        call?.removePeerVideoView(peerVideoView)
    }

    fun muteOn(on: Boolean) {
        call?.muteMyAudio(on)
    }

    fun pauseMyVideo() {
        call?.pauseMyVideo(PlanetKitVideoPauseReason.UNDEFINED)
    }

    fun resumeMyVideo() {
        call?.resumeMyVideo()
    }

    fun verifyCall(myUserId: String, myServiceId: String, cCParamStr: String,
                   holdToneUri: Uri?, ringToneUri: Uri?, endToneUri: Uri?,
                   callback: OneOnOneVerifyCallback
    ): PlanetKitCallResult? {
        if (cCParamStr.isNullOrBlank()) {
            Log.e(TAG, "Empty message from application server")
            return null
        }

        val cCParam = PlanetKitCCParam.create(cCParamStr)
        if (cCParam == null) {
            Log.e(TAG, "Can not create PlanetKitCCParam. Check cCParamStr=$cCParamStr")
            return null
        }

        val paramBuilder = PlanetKitVerifyCallParam.Builder()
            .myId(myUserId)
            .myServiceId(myServiceId)
            .cCParam(cCParam)

        if (holdToneUri != null) paramBuilder.holdTone(holdToneUri)
        if (ringToneUri != null) paramBuilder.ringTone(ringToneUri)
        if (endToneUri != null) paramBuilder.endTone(endToneUri)

        val result = PlanetKit.verifyCall(paramBuilder.build(), object: VerifyListener {
            override fun onVerified(call: PlanetKitCall, peerStartMessage: PlanetKitCallStartMessage?, peerUseResponderPreparation: Boolean) {
                callStateModel.update(repositoryContainer.context, call.state)
                callback.onVerified(call.instanceId, call.peer.userId, call.mediaType.isVideo())
            }
            override fun onDisconnected(
                call: PlanetKitCall,
                param: PlanetKitDisconnectedParam
            ) {
                Log.i(TAG, "onDisconnected reason: $param")
                callStateModel.update(repositoryContainer.context, call.state, param.reason.name)
                callback.onDisconnected(call.instanceId)
                repositoryContainer.removeRepository(call.instanceId)
            }
        })

        if (result.reason == PlanetKitStartFailReason.NONE) {
            call = result.call
            repositoryContainer.setRepository(call!!.instanceId, this)
        }

        return result
    }

    fun makeCall(param: PlanetKitMakeCallParam): PlanetKitCallResult {
        val result = PlanetKit.makeCall(param, callListener)
        if (result.reason == PlanetKitStartFailReason.NONE) {
            call = result.call
            repositoryContainer.setRepository(call!!.instanceId, this)
        }
        return result
    }

    fun endCall() {
        call?.endCall()
    }

    fun acceptCall() {
        call?.acceptCall(callListener)
    }

    private val callListener = object: MakeCallListener, AcceptCallListener {
        override fun onConnected(call: PlanetKitCall, param: PlanetKitCallConnectedParam) {
            callStateModel.update(repositoryContainer.context, PlanetKitCallState.CONNECTED)
        }

        override fun onDisconnected(call: PlanetKitCall, param: PlanetKitDisconnectedParam) {
            callStateModel.update(repositoryContainer.context, PlanetKitCallState.END, param.reason.name)
            repositoryContainer.removeRepository(call.instanceId)
        }

        override fun onWaitConnected(call: PlanetKitCall) {
            callStateModel.update(repositoryContainer.context, PlanetKitCallState.WAITANSWER)
        }

        override fun onPeerMicMuted(call: PlanetKitCall) {
            peerStateModel.updateMute(true)
        }

        override fun onPeerMicUnmuted(call: PlanetKitCall) {
            peerStateModel.updateMute(false)
        }

        override fun onPeerVideoPaused(call: PlanetKitCall, reason: PlanetKitVideoPauseReason) {
            peerStateModel.updateVideoStatus(UIKitVideoStatus.PAUSED)
        }

        override fun onPeerVideoResumed(call: PlanetKitCall) {
            peerStateModel.updateVideoStatus(UIKitVideoStatus.ENABLED)
        }

        override fun onVideoEnabledByPeer(call: PlanetKitCall) {
            peerStateModel.updateVideoStatus(UIKitVideoStatus.ENABLED)
        }

        override fun onVideoDisabledByPeer(call: PlanetKitCall, reason: PlanetKitMediaDisableReason) {
            peerStateModel.updateVideoStatus(UIKitVideoStatus.DISABLED)
        }

        override fun onPeerAudioDescriptionUpdated(call: PlanetKitCall,
            audioDescription: PlanetKitAudioDescription) {
            peerStateModel.updateAudioDescription(audioDescription)
        }
    }

    companion object {
        private const val TAG = "${StringSet.LOG_UIKIT}PlanetKitOneOnOneCallRepository"
    }

}