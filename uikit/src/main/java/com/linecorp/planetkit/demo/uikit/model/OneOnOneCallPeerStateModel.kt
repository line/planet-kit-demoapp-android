package com.linecorp.planetkit.demo.uikit.model

import com.linecorp.planetkit.audio.PlanetKitAudioDescription
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitVideoStatus

class OneOnOneCallPeerStateModel {
    private var _isMute = false
    private var _videoStatus = UIKitVideoStatus.DISABLED
    private var _averageVolume = 0

    val isMute: Boolean
        get() = _isMute

    val videoStatus: UIKitVideoStatus
        get() = _videoStatus

    val averageVolume: Int
        get() = _averageVolume


    private val listenerList = ArrayList<Listener>()

    internal fun updateMute(value: Boolean) {
        _isMute = value
        listenerList.forEach { it.onMuteChanged(value) }
    }

    internal fun updateVideoStatus(status: UIKitVideoStatus) {
        _videoStatus = status
        listenerList.forEach { it.onVideoStatusChanged(status) }
    }

    internal fun updateAudioDescription(audioDescription: PlanetKitAudioDescription) {
        _averageVolume = audioDescription.averageVolumeLevel
        listenerList.forEach { it.onAverageVolumeLevelChanged(_averageVolume) }
    }


    fun addStateListener(listener: Listener) {
        listenerList.add(listener)
    }

    fun removeStateListener(listener: Listener) {
        listenerList.remove(listener)
    }

    interface Listener {
        fun onMuteChanged(muteOn: Boolean)
        fun onVideoStatusChanged(videoStatus: UIKitVideoStatus)
        fun onAverageVolumeLevelChanged(volume: Int)
    }
}