package com.linecorp.planetkit.demo.uikit.repositories

import android.content.Context
import com.linecorp.planetkit.PlanetKit
import com.linecorp.planetkit.PlanetKitLogLevel
import com.linecorp.planetkit.PlanetKitLogSizeLimit
import com.linecorp.planetkit.demo.uikit.consts.StringSet

class PlanetKitRepository {
    private var _isVideoHwCodecSupport: Boolean = false
    private var _userAgent: String? = null

    val isVideoHwCodecSupport: Boolean
        get() = _isVideoHwCodecSupport

    val userAgent: String?
        get() = _userAgent

    val sdkVersion: String
        get() = PlanetKit.version
    val isInitialized: Boolean
        get() = PlanetKit.isInitialize

    fun initialize(context: Context, cloudUrl: String, enableLog: Boolean, listener: PlanetKit.OnInitializeCompleteListener) {
        val config = PlanetKit.PlanetKitConfiguration.Builder(context)
            .enableLog(true)
            .setServerUrl(cloudUrl)
            .enableLog(enableLog)
            .setLogSizeLimit(PlanetKitLogSizeLimit.LARGE)
            .setLogLevel(PlanetKitLogLevel.DETAILED)
            .build()
        PlanetKit.initialize(config){ isSuccessful, isVideoHwCodecSupport, userAgent ->
            _isVideoHwCodecSupport = isVideoHwCodecSupport
            _userAgent = userAgent
            listener.onComplete(isSuccessful, isVideoHwCodecSupport, userAgent)
        }
    }

    private var _cameraRepository: PlanetKitCameraRepository? = null
    val cameraRepository: PlanetKitCameraRepository
        get() {
            val prevVideoSource = _cameraRepository?.cameraVideoSource
            val currentVideoSource = PlanetKit.getDefaultCameraVideoSource()

            if (prevVideoSource != currentVideoSource) {
                _cameraRepository = PlanetKitCameraRepository(currentVideoSource)
            }
            return _cameraRepository!!
        }

    companion object {
        private const val TAG = "${StringSet.LOG_UIKIT}PlanetKitRepository"
    }
}