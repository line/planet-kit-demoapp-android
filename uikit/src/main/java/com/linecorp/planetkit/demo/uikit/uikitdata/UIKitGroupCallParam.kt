package com.linecorp.planetkit.demo.uikit.uikitdata

import java.io.Serializable

data class UIKitGroupCallParam(
    val myId: String,
    val myDisplayName: String,
    val myServiceId: String,
    val roomId: String,
    val roomServiceId: String,
    var isVideo: Boolean,
    var muteOnStart: Boolean? = false,
) : Serializable