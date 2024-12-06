package com.linecorp.planetkit.demo.uikit.model

interface OneOnOneVerifyCallback {
    fun onVerified(callInstanceId: Int, peerUserId: String, isVideoCall: Boolean)
    fun onDisconnected(callInstanceId: Int)
}