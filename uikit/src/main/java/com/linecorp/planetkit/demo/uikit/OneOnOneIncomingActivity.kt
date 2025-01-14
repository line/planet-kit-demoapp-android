package com.linecorp.planetkit.demo.uikit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.linecorp.planetkit.demo.uikit.fragments.OneOnOneIncomingCallFragment
import com.linecorp.planetkit.demo.uikit.fragments.SingleButtonFragmentDialog

class OneOnOneIncomingActivity: AppCompatActivity() {
    var callInstanceId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_on_one_incoming_call)

        callInstanceId = intent.getIntExtra(INSTANCE_ID_KEY, -1)
        showIncomingCallFragment(callInstanceId)
    }

    private fun showIncomingCallFragment(callInstanceId: Int) {
        val fragment = OneOnOneIncomingCallFragment(callInstanceId,
            object: OneOnOneIncomingCallFragment.OnEventListener {
                override fun onConnected(isVoiceCall: Boolean) {
                    OneOnOneCallActivity.start(this@OneOnOneIncomingActivity, callInstanceId, !isVoiceCall, false)
                    finish()
                }

                override fun onDisconnected(reason: String) {
                    showEndCallDialog(reason)
                }
            }
        )
        supportFragmentManager.beginTransaction()
            .replace(R.id.incoming_call_fragment_container, fragment).commit()
    }

    private fun showEndCallDialog(reason: String) {
        val contents = getString(R.string.lp_demoapp_1to1_scenarios_basic_endcall2) + "\n($reason)"
        SingleButtonFragmentDialog(
            getString(R.string.lp_demoapp_1to1_scenarios_basic_endcall1),
            contents,
            getString(R.string.lp_demoapp_1to1_scenarios_basic_endcall3)
        ) {
            setResult(GroupCallActivity.ACTIVITY_RESULT_END)
            finish()
        }.show(supportFragmentManager, null)
    }

    companion object {
        private const val INSTANCE_ID_KEY = "INSTANCE_ID_KEY"

        @JvmStatic
        fun getIntent(context: Context, instanceId: Int): Intent {
            return Intent(context, OneOnOneIncomingActivity::class.java).apply {
                putExtra(INSTANCE_ID_KEY, instanceId)
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
        fun start(context: Context, instanceId: Int) {
            val intent = getIntent(context, instanceId)
            context.startActivity(intent)
        }
    }
}