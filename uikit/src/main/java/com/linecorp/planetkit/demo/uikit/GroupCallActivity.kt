package com.linecorp.planetkit.demo.uikit

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.linecorp.planetkit.demo.uikit.databinding.ActivityGroupCallBinding
import com.linecorp.planetkit.demo.uikit.fragments.GroupCallFragment
import com.linecorp.planetkit.demo.uikit.fragments.GroupCallPreviewFragment
import com.linecorp.planetkit.demo.uikit.fragments.SingleButtonFragmentDialog
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitGroupCallParam
import com.linecorp.planetkit.demo.uikit.utils.observe
import com.linecorp.planetkit.demo.uikit.viewmodels.GroupCallStateViewModel

class GroupCallActivity: AppCompatActivity() {
    private var isConnected = false

    private val binding by lazy {
        ActivityGroupCallBinding.inflate(layoutInflater)
    }

    private val groupCallStateViewModel: GroupCallStateViewModel by viewModels {
        GroupCallStateViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val param = intent.getSerializableExtra(UIKIT_GROUP_CALL_PARAM) as UIKitGroupCallParam?
        if (param == null)
        {
            setResult(ACTIVITY_RESULT_END)
            finish()
            return
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.group_call_container, GroupCallPreviewFragment(param){
                setResult(if (it) ACTIVITY_RESULT_UNHANDLED_EXCEPTION else ACTIVITY_RESULT_CANCELED)
                finish()
            })
            .commit()

        observe(groupCallStateViewModel.isConnected) {
            when(it) {
                true -> {
                    isConnected = true
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.group_call_container, GroupCallFragment())
                        .commit()
                }
                false -> {
                    if (isConnected) {
                        val contents = getString(R.string.lp_demoapp_1to1_scenarios_basic_endcall2) + "\n(${groupCallStateViewModel.disconnectReason})"
                        SingleButtonFragmentDialog(
                            this.getString(R.string.lp_demoapp_1to1_scenarios_basic_endcall1),
                            contents,
                            this.getString(R.string.lp_demoapp_1to1_scenarios_basic_endcall3)
                        ) {
                            setResult(ACTIVITY_RESULT_END)
                            finish()
                        }.show(supportFragmentManager, null)
                    }
                }
                else -> {}
            }
        }

        this.onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
            }

        })
    }

    companion object {
        const val UIKIT_GROUP_CALL_PARAM = "UKitGroupCallParam"
        const val ACTIVITY_RESULT_UNHANDLED_EXCEPTION = -2
        const val ACTIVITY_RESULT_NO_PARAM = -1
        const val ACTIVITY_RESULT_END = 0
        const val ACTIVITY_RESULT_CANCELED = 1
    }
}