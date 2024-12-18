package com.linecorp.planetkit.demo.uikit

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.linecorp.planetkit.demo.uikit.consts.StringSet
import com.linecorp.planetkit.demo.uikit.model.OneOnOneVerifyCallback
import com.linecorp.planetkit.demo.uikit.usecases.LongPollingNotificationUseCase
import com.linecorp.planetkit.demo.uikit.usecases.OneOnOneVerifyCallUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException


class LongPollingNotificationService : Service() {
    companion object {
        private const val TAG = "${StringSet.LOG_UIKIT}LongPollingNotificationService"
        const val CHANNEL_ID = "ForegroundServiceChannel"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        private const val RESPONSE_CODE_NO_CALL = 503
    }

    private var job: Job? = null
    private var isRunning = false

    private val longPollingNotificationUseCase by lazy {
        LongPollingNotificationUseCase(
            (application as UiKitApplication).appContainer.appServerRepository,
            (application as UiKitApplication).appContainer.serviceLocatorRepository
        )
    }

    private val oneOnOneVerifyCallUseCase by lazy {
        OneOnOneVerifyCallUseCase(
            (application as UiKitApplication).appContainer.kitRepository,
            (application as UiKitApplication).appContainer.serviceLocatorRepository,
            (application as UiKitApplication).appContainer.callRepositoryContainer
        )
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && ACTION_STOP_SERVICE == intent.action) {
            job?.cancel()
            stopForeground(true)
            stopSelf()
            return START_NOT_STICKY
        }

        if (isRunning) {
            return START_NOT_STICKY
        }
        isRunning = true

        val stopIntent = Intent(this, LongPollingNotificationService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }
        val stopPendingIntent = PendingIntent.getService(this, 0, stopIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Waiting for call")
            .setSmallIcon(R.drawable.icon_planetkit)
            .addAction(R.drawable.icon_planetkit, "Stop", stopPendingIntent)
            .build()

        startForeground(1, notification)

        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                if (!longPollingNotificationUseCase.isLongPollingAvailable) {
                    Log.e(TAG, "long polling is unavailable")
                    job?.cancel()
                    stopForeground(true)
                    stopSelf()
                    return@launch
                }

                try {
                    val result = longPollingNotificationUseCase.longPollingNotification()
                    if (result.code == RESPONSE_CODE_NO_CALL) {
                        continue
                    }

                    if (result.cCParam?.isNotEmpty() == true) {
                        oneOnOneVerifyCallUseCase.verifyCall(
                            result.cCParam,
                            object : OneOnOneVerifyCallback {
                                override fun onVerified(
                                    callInstanceId: Int,
                                    peerUserId: String,
                                    isVideoCall: Boolean
                                ) {
                                    NotificationService.showRinging(
                                        this@LongPollingNotificationService,
                                        callInstanceId,
                                        peerUserId,
                                        isVideoCall
                                    )

                                    // Start activity from Service is prohibited from Android 10
                                    if (Build.VERSION.SDK_INT < 29 || isAppVisible()) {
                                        OneOnOneIncomingActivity.start(
                                            this@LongPollingNotificationService,
                                            callInstanceId
                                        )
                                    }
                                }

                                override fun onDisconnected(callInstanceId: Int) {
                                    NotificationService.clear(this@LongPollingNotificationService)
                                }
                            })
                        continue
                    }
                } catch (e: HttpException) {
                    if (e.code() == RESPONSE_CODE_NO_CALL) {
                        continue
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Long polling exception [${e.message}]")
                }

                delay(5000)
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun isAppVisible(): Boolean {
        return ProcessLifecycleOwner
            .get()
            .lifecycle
            .currentState
            .isAtLeast(Lifecycle.State.STARTED)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}