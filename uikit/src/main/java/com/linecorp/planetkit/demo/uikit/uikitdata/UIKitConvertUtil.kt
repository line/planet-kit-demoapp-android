package com.linecorp.planetkit.demo.uikit.uikitdata

import com.linecorp.planetkit.video.PlanetKitVideoStatus

object UIKitConvertUtil {
    fun convertVideoStatus(v: PlanetKitVideoStatus): UIKitVideoStatus {
        return when (v.videoState) {
            PlanetKitVideoStatus.VideoState.ENABLED -> UIKitVideoStatus.ENABLED
            PlanetKitVideoStatus.VideoState.DISABLED -> UIKitVideoStatus.DISABLED
            PlanetKitVideoStatus.VideoState.PAUSED -> UIKitVideoStatus.PAUSED
        }
    }
}