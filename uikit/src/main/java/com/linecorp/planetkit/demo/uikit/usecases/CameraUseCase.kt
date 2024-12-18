package com.linecorp.planetkit.demo.uikit.usecases

import android.util.Log
import com.linecorp.planetkit.demo.uikit.consts.StringSet
import com.linecorp.planetkit.demo.uikit.repositories.PlanetKitRepository
import com.linecorp.planetkit.ui.PlanetKitVideoView
import com.linecorp.planetkit.video.CameraVideoSource
import com.linecorp.planetkit.video.PlanetKitCameraType


class CameraUseCase(planetKitRepository: PlanetKitRepository): UseCase {
    private val cameraRepository = planetKitRepository.cameraRepository
    val isFrontCamera: Boolean
        get() = cameraRepository.cameraType == PlanetKitCameraType.FRONT


    val isStarted: Boolean
        get() = cameraRepository.isStarted

    private var listener: Listener? = null

    private val stateListener = object: CameraVideoSource.StateListener{
        override fun onError(source: CameraVideoSource, code: Int) {
            Log.e(TAG, "onError(${this.hashCode()}) code=$code")
        }

        override fun onStart(source: CameraVideoSource) {
            listener?.onStart()
        }

        override fun onStop(source: CameraVideoSource) {
            listener?.onStop()
        }
    }

    init {
        cameraRepository.addStateListener(stateListener)
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun start() {
        cameraRepository.start()
    }

    fun stop() {
        cameraRepository.stop()
    }

    fun addVideoView(videoView: PlanetKitVideoView) {
        cameraRepository.addMyVideoView(videoView)
    }

    fun removeCameraVideoView(videoView: PlanetKitVideoView) {
        cameraRepository.removeMyVideoView(videoView)
    }

    fun setCameraType(isFront: Boolean) {
        cameraRepository.cameraType = if (isFront) PlanetKitCameraType.FRONT else PlanetKitCameraType.BACK
    }
    override fun onCleared() {
        cameraRepository.removeStateListener(stateListener)
        listener = null
    }

    interface Listener {
        fun onStart()
        fun onStop()
    }

    companion object {
        private const val TAG = "${StringSet.LOG_UIKIT}CameraUseCase"
    }
}