package com.linecorp.planetkit.demo.uikit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.linecorp.planetkit.demo.uikit.fragments.OneOnOneVideoCallFragment
import com.linecorp.planetkit.demo.uikit.fragments.OneOnOneVideoCallWaitAnswerFragment
import com.linecorp.planetkit.demo.uikit.fragments.OneOnOneVoiceCallFragment
import com.linecorp.planetkit.demo.uikit.fragments.SingleButtonFragmentDialog
import com.linecorp.planetkit.demo.uikit.utils.Permissions

class OneOnOneCallActivity: AppCompatActivity() {
    private var callInstanceId: Int = -1
    private var isVideoCall: Boolean = false
    private var isMakeCall: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_on_one_call)

        callInstanceId = intent.getIntExtra(KEY_INSTANCE_ID, -1)
        isVideoCall = intent.getBooleanExtra(KEY_IS_VIDEO_CALL, false)
        isMakeCall = intent.getBooleanExtra(KEY_IS_MAKE_CALL, false)

        showCallFragment(callInstanceId, isVideoCall, isMakeCall)
        showCallingNotification(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val dialogFragment = supportFragmentManager.findFragmentByTag(END_CALL_DIALOG_TAG) as? DialogFragment
        dialogFragment?.dismiss()

        callInstanceId = intent.getIntExtra(KEY_INSTANCE_ID, callInstanceId)
        isVideoCall = intent.getBooleanExtra(KEY_IS_VIDEO_CALL, isVideoCall)
        isMakeCall = intent.getBooleanExtra(KEY_IS_MAKE_CALL, isMakeCall)
        showCallFragment(callInstanceId, isVideoCall, isMakeCall)
    }

    private fun showCallFragment(callInstanceId: Int, isVideoCall: Boolean, isMakeCall: Boolean) {
        val fragment = if (isVideoCall) {
            if (isMakeCall) {
                OneOnOneVideoCallWaitAnswerFragment(callInstanceId, object: OneOnOneVideoCallWaitAnswerFragment.OnEventListener {
                    override fun onConnected() {
                        val videoCallFragment = OneOnOneVideoCallFragment(callInstanceId) {
                            showEndCallDialog(it)
                        }
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.call_fragment_container, videoCallFragment).commit()
                    }

                    override fun onDisconnected(reason: String) {
                        showEndCallDialog(reason)
                    }
                })
            }
            else {
                OneOnOneVideoCallFragment(callInstanceId) {
                    showEndCallDialog(it)
                }
            }
        }
        else {
            OneOnOneVoiceCallFragment(callInstanceId) {
                showEndCallDialog(it)
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.call_fragment_container, fragment).commit()
    }

    private fun showEndCallDialog(reason: String) {
        val contents = getString(R.string.lp_demoapp_1to1_scenarios_basic_endcall2) + "\n($reason)"
        SingleButtonFragmentDialog(
            getString(R.string.lp_demoapp_1to1_scenarios_basic_endcall1),
            contents,
            getString(R.string.lp_demoapp_1to1_scenarios_basic_endcall3)
        ) {
            finish()
        }.show(supportFragmentManager, END_CALL_DIALOG_TAG)
    }

    private fun showCallingNotification(context: Context) {
        NotificationService.showOngoingSession(
            context,
            context.getString(R.string.call_in_progress),
            false,
            Permissions.hasMicrophonePermission(context)
        )
    }

    companion object {
        private const val KEY_INSTANCE_ID = "KEY_INSTANCE_ID"
        private const val KEY_IS_VIDEO_CALL = "KEY_IS_VIDEO_CALL"
        private const val KEY_IS_MAKE_CALL = "KEY_IS_MAKE_CALL"
        private const val END_CALL_DIALOG_TAG = "EndCallDialog"

        @JvmStatic
        fun getIntent(context: Context, instanceId: Int, isVideoCall: Boolean, isMakeCall: Boolean): Intent {
            return Intent(context, OneOnOneCallActivity::class.java).apply {
                putExtra(KEY_INSTANCE_ID, instanceId)
                putExtra(KEY_IS_VIDEO_CALL, isVideoCall)
                putExtra(KEY_IS_MAKE_CALL, isMakeCall)
                addFlags(
                    Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
                        or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                        or Intent.FLAG_ACTIVITY_NEW_TASK
                )
            }
        }

        @JvmStatic
        fun start(context: Context, instanceId: Int, isVideoCall: Boolean, isMakeCall: Boolean) {
            val intent = getIntent(context, instanceId, isVideoCall, isMakeCall)
            context.startActivity(intent)
        }
    }
}