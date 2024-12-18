package com.linecorp.planetkit.demo.uikit

import android.app.Application
import android.content.ContentResolver
import android.net.Uri

abstract class UiKitApplication (
    private val planetCloudUrl: String,
    appServerUrl: String,
    serviceId: String,
    region: String,
    apiKey: String,
    endToneResId: Int? = null,
    holdToneResId: Int? = null,
    ringBackToneResId: Int? = null,
    ringToneResId: Int? = null,

): Application() {
    val appContainer = UikitAppContainer(
        this,
        appServerUrl,
        serviceId,
        region,
        apiKey,
        endToneResId,
        holdToneResId,
        ringBackToneResId,
        ringToneResId,
    )

    override fun onCreate() {
        super.onCreate()
        appContainer.initialize(applicationContext, planetCloudUrl, true)
    }
}