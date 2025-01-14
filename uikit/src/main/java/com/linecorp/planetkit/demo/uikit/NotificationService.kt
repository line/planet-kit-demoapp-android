package com.linecorp.planetkit.demo.uikit

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.*
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.linecorp.planetkit.demo.uikit.consts.StringSet
class NotificationService : Service() {
    companion object {
        const val TAG = "${StringSet.LOG_UIKIT}NotificationService"

        private const val KEY_INSTANCE_ID = "KEY_INSTANCE_ID"
        private const val KEY_PEER_USER_ID = "KEY_PEER_USER_ID"
        private const val KEY_IS_VIDEO_CALL = "KEY_IS_VIDEO_CALL"

        private const val INCOMING_CHANNEL_ID = "Incoming call"
        private const val ONGOING_CHANNEL_ID = "Call in Progress"

        // This can be any dummy number
        private const val NOTIFICATION_ID = 314

        private const val ACTION_RINGING = "ACTION_RINGING"
        private const val ACTION_CONNECT_SESSION = "ACTION_CONNECT_SESSION"
        private const val ACTION_CLEAR = "ACTION_CLEAR"

        private const val MESSAGE_KEY = "MESSAGE_KEY"
        private const val IS_CONFERENCE_KEY = "IS_CONFERENCE_KEY"
        private const val HAS_MIC_PERMISSION = "HAS_MIC_PERMISSION"

        @Synchronized
        @JvmStatic
        fun showOngoingSession(context: Context, message: String, isConference: Boolean, hasMicPermission: Boolean) {
            val intent = Intent(context, NotificationService::class.java).apply {
                action = ACTION_CONNECT_SESSION
                putExtra(MESSAGE_KEY, message)
                putExtra(IS_CONFERENCE_KEY, isConference)
                putExtra(HAS_MIC_PERMISSION, hasMicPermission)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        @Synchronized
        @JvmStatic
        fun showRinging(context: Context, instanceId: Int, peerUserId: String, isVideoCall: Boolean) {
            val intent = Intent(context, NotificationService::class.java).apply {
                action = ACTION_RINGING
                putExtra(KEY_INSTANCE_ID, instanceId)
                putExtra(KEY_PEER_USER_ID, peerUserId)
                putExtra(KEY_IS_VIDEO_CALL, isVideoCall)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        @Synchronized
        @JvmStatic
        fun clear(context: Context) {
            val intent = Intent(context, NotificationService::class.java).apply {
                action = ACTION_CLEAR
            }
            context.startService(intent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY
        when (action) {
            ACTION_RINGING -> {
                onRinging(intent)
            }
            ACTION_CONNECT_SESSION -> {
                onConnectSession(intent)
            }
            ACTION_CLEAR -> {
                onClearRequested()
            }
        }
        return START_NOT_STICKY
    }

    private fun onClearRequested() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(Service.STOP_FOREGROUND_REMOVE)
        }
        else @Suppress("DEPRECATION") {
            stopForeground(true)
        }
        stopSelf()
    }

    private fun onConnectSession(incomingIntent: Intent) {
        createChannel(ONGOING_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW)

        val isConference = incomingIntent.getBooleanExtra(IS_CONFERENCE_KEY, false)
        val hasMicPermission = incomingIntent.getBooleanExtra(HAS_MIC_PERMISSION, false)
        val newIntent =
            if (isConference) Intent(this, GroupCallActivity::class.java)
            else Intent(this, OneOnOneCallActivity::class.java)

        val pendingFlags: Int = if (Build.VERSION.SDK_INT >= 23) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, newIntent, pendingFlags)
        val message = incomingIntent.getStringExtra(MESSAGE_KEY) ?: "Unknown"

        val builder = NotificationCompat.Builder(
            this,
            ONGOING_CHANNEL_ID
        )
            .setContentTitle(message)
            .setSmallIcon(R.drawable.icon_planetkit)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_STATUS)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val serviceType = if (hasMicPermission) {
                FOREGROUND_SERVICE_TYPE_PHONE_CALL or FOREGROUND_SERVICE_TYPE_MICROPHONE
            }
            else {
                FOREGROUND_SERVICE_TYPE_PHONE_CALL or FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            }

            startForeground(
                NOTIFICATION_ID,
                builder.build(),
                serviceType
            )
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                builder.build(),
                FOREGROUND_SERVICE_TYPE_PHONE_CALL or FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        }
        else {
            startForeground(
                NOTIFICATION_ID,
                builder.build()
            )
        }
    }

    private fun onRinging(incomingIntent: Intent) {
        createChannel(INCOMING_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_HIGH)

        val instanceId = incomingIntent.getIntExtra(KEY_INSTANCE_ID, -1)
        val isVideoCall = incomingIntent.getBooleanExtra(KEY_IS_VIDEO_CALL, false)
        if (instanceId == -1) {
            return
        }

        val fullScreenPi = PendingIntent.getActivity(
            this,
            0,
            OneOnOneIncomingActivity.getIntent(this, instanceId),
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val title =
            if (isVideoCall) getString(R.string.lp_demoapp_1to1_noti_video)
            else getString(R.string.lp_demoapp_1to1_noti_voice)
        val peerUserId = incomingIntent.getStringExtra(KEY_PEER_USER_ID)
        val builder = NotificationCompat.Builder(this, INCOMING_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.icon_planetkit)
            setContentTitle(title)
            setContentText(peerUserId)
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setCategory(NotificationCompat.CATEGORY_CALL)
            setFullScreenIntent(fullScreenPi, true)
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID, builder.build(), FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
            }
            else {
                startForeground(NOTIFICATION_ID, builder.build())
            }
        }
        catch (e: Exception) {
            Log.e(TAG, "onRinging:startForegroundExtension error=${e.message}")
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(this,
                    "Call verified without appropriate permissions",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createChannel(
        channelId: String,
        importance: Int
    ) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelId,
                importance
            )
            channel.enableLights(true)
            channel.enableVibration(true)
            // Register the channel with the system
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    override fun onBind(p0: Intent?): IBinder? = null
}