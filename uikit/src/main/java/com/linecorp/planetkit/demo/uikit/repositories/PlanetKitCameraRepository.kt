package com.linecorp.planetkit.demo.uikit.repositories

import com.linecorp.planetkit.ui.PlanetKitVideoView
import com.linecorp.planetkit.video.CameraVideoSource
import com.linecorp.planetkit.video.PlanetKitCameraType

class PlanetKitCameraRepository(val cameraVideoSource: CameraVideoSource) {

    var cameraType: PlanetKitCameraType
        get() = cameraVideoSource.cameraType
        set(type) {
            cameraVideoSource.cameraType = type
        }

    val isStarted: Boolean
        get() = cameraVideoSource.isStarted

    fun addStateListener(listener: CameraVideoSource.StateListener) {
        cameraStateListeners.add(listener)
    }

    fun removeStateListener(listener: CameraVideoSource.StateListener) {
        cameraStateListeners.remove(listener)
    }

    fun start() {
        cameraVideoSource.start()
    }

    fun stop() {
        cameraVideoSource.stop()
    }

    fun addMyVideoView(view: PlanetKitVideoView) {
        cameraVideoSource.addMyVideoView(view)
    }

    fun removeMyVideoView(view: PlanetKitVideoView) {
        cameraVideoSource.removeMyVideoView(view)
    }

    private val cameraStateListeners: MutableList<CameraVideoSource.StateListener> = mutableListOf()

    private val stateListener = object: CameraVideoSource.StateListener{
        override fun onError(source: CameraVideoSource, code: Int) {
            cameraStateListeners.forEach {
                it.onError(source, code)
            }
        }

        override fun onStart(source: CameraVideoSource) {
            cameraStateListeners.forEach {
                it.onStart(source)
            }
        }

        override fun onStop(source: CameraVideoSource) {
            cameraStateListeners.forEach {
                it.onStop(source)
            }
        }
    }

    init {
        cameraVideoSource.setStateListener(stateListener)
    }
}