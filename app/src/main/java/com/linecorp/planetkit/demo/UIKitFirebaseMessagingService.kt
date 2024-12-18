package com.linecorp.planetkit.demo

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.linecorp.planetkit.demo.uikit.NotificationService
import com.linecorp.planetkit.demo.uikit.OneOnOneIncomingActivity
import com.linecorp.planetkit.demo.uikit.UiKitApplication
import com.linecorp.planetkit.demo.uikit.consts.StringSet
import com.linecorp.planetkit.demo.uikit.model.OneOnOneVerifyCallback
import com.linecorp.planetkit.demo.uikit.usecases.UpdateNotificationTokenUseCase
import com.linecorp.planetkit.demo.uikit.usecases.OneOnOneVerifyCallUseCase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class UIKitFirebaseMessagingService : FirebaseMessagingService() {
    private val updateNotificationTokenUseCase by lazy {
        UpdateNotificationTokenUseCase(
            (application as UiKitApplication).appContainer.appServerRepository,
            (application as UiKitApplication).appContainer.serviceLocatorRepository,
        )
    }

    private val oneOnOneVerifyCallUseCase by lazy {
        OneOnOneVerifyCallUseCase(
            (application as UiKitApplication).appContainer.kitRepository,
            (application as UiKitApplication).appContainer.serviceLocatorRepository,
            (application as UiKitApplication).appContainer.callRepositoryContainer
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        MainScope().launch {
            try {
                updateNotificationTokenUseCase.silentRegisterToken(token, ServiceConstants.NOTIFICATION_TYPE)
            } catch (e: Exception) {
                val errorLog = "$e \n${Log.getStackTraceString(java.lang.Exception())}"
                Log.e(TAG, errorLog)
                throw e
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val handleMessage = eventHandler.obtainMessage(MSG_RECEIVED).apply {
            obj = message
        }
        eventHandler.sendMessage(handleMessage)
    }

    private fun verifyCall(message: RemoteMessage) {
        oneOnOneVerifyCallUseCase.verifyCall(message,
            object: OneOnOneVerifyCallback{
                override fun onVerified(callInstanceId: Int, peerUserId: String, isVideoCall: Boolean) {
                    NotificationService.showRinging(
                        this@UIKitFirebaseMessagingService,
                        callInstanceId,
                        peerUserId,
                        isVideoCall
                    )

                    // Start activity from Service is prohibited from Android 10
                    if (Build.VERSION.SDK_INT < 29 || isAppVisible()) {
                        OneOnOneIncomingActivity.start(
                            this@UIKitFirebaseMessagingService,
                            callInstanceId
                        )
                    }
                }

                override fun onDisconnected(callInstanceId: Int) {
                    NotificationService.clear(this@UIKitFirebaseMessagingService)
                }
            })
    }

    private val eventHandler = EventHandler(Looper.getMainLooper())
    private inner class EventHandler(looper: Looper): Handler(looper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_RECEIVED -> {
                    val receivedMessage = msg.obj
                    if (oneOnOneVerifyCallUseCase.isPlanetKitInitialized) {
                        if (receivedMessage is RemoteMessage) {
                            verifyCall(receivedMessage)
                        }
                    }
                    else {
                        val message = eventHandler.obtainMessage(MSG_RECEIVED).apply {
                            obj = receivedMessage
                        }
                        sendMessageDelayed(message, 100)
                    }
                }
            }
        }
    }

    private fun isAppVisible(): Boolean {
        return ProcessLifecycleOwner
            .get()
            .lifecycle
            .currentState
            .isAtLeast(Lifecycle.State.STARTED)
    }

    companion object {
        private const val TAG = "${StringSet.LOG_UIKIT}UIKitFirebaseMessagingService"
        private const val MSG_RECEIVED = 100
    }
}
