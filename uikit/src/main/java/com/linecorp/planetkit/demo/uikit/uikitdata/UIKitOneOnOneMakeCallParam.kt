package com.linecorp.planetkit.demo.uikit.uikitdata

import android.net.Uri
import com.linecorp.planetkit.PlanetKitMediaType
import com.linecorp.planetkit.session.call.PlanetKitMakeCallParam

data class UIKitOneOnOneMakeCallParam(
    private val myId: String,
    private val myServiceId: String,
    private val peerId: String,
    private val peerServiceId: String,
    private val accessToken: String,
    private val isVideo: Boolean,
    private val endTone: Uri? = null,
    private val holdTone: Uri?,
    private val ringBackTone: Uri?,
) {
    internal fun transToPlanetKitParam(): PlanetKitMakeCallParam {
        val paramBuilder =  PlanetKitMakeCallParam.Builder()
            .myId(myId)
            .myServiceId(myServiceId)
            .peerId(peerId)
            .peerServiceId(peerServiceId)
            .accessToken(accessToken)
            .mediaType(
                if (!isVideo) PlanetKitMediaType.AUDIO else PlanetKitMediaType.AUDIOVIDEO
            )
        if (endTone != null) paramBuilder.endTone(endTone)
        if (holdTone != null) paramBuilder.holdTone(holdTone)
        if (ringBackTone != null) paramBuilder.ringbackTone(ringBackTone)
        return paramBuilder.build()
    }
}