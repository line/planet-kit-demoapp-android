package com.linecorp.planetkit.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.linecorp.planetkit.demo.databinding.FragmentMakeOneOnOneCallBinding
import com.linecorp.planetkit.demo.uikit.OneOnOneCallActivity
import com.linecorp.planetkit.demo.uikit.fragments.SingleButtonFragmentDialog
import com.linecorp.planetkit.demo.uikit.utils.observe
import com.linecorp.planetkit.demo.uikit.viewmodels.OneOnOneMakeCallViewModel
import kotlinx.coroutines.launch

class OneOnOneMakeCallFragment: Fragment() {
    private val binding by lazy {
        FragmentMakeOneOnOneCallBinding.inflate(layoutInflater)
    }

    private val oneOnOneMakeCallViewModel: OneOnOneMakeCallViewModel by viewModels {
        OneOnOneMakeCallViewModel.Factory
    }

    private val versionInfoViewModel: VersionInfoViewModel by viewModels {
        VersionInfoViewModel.Factory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.btnPrev.setOnClickListener {
            it.findNavController().navigate(R.id.action_make_call_to_call_features)
        }

        binding.btnHome.setOnClickListener {
            it.findNavController().navigate(R.id.action_make_call_to_main)
        }

        binding.btnVoiceCall.setOnClickListener {
            makeCall(false)
        }

        binding.btnVideoCall.setOnClickListener {
            makeCall(true)
        }

        binding.tvSdkVersion.text = sdkVersion
        binding.tvAppVersion.text = appVersion

        observe(oneOnOneMakeCallViewModel.makeCallResult) { result ->
            result?.let {
                if (it.isSuccessful) {
                    val callInstanceId = it.callInstanceId
                    OneOnOneCallActivity.start(this.requireActivity(), callInstanceId, it.isVideoCall, true)
                }
                else {
                    val contents = when (it.planetKitStartFailReason) {
                        "KIT_PEER_USER_ID_BLANK" -> this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_common_error_startfail1)
                        null -> this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_common_default_error_msg) + "(${it.exceptionMsg})"
                        else -> this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_common_default_error_msg) + "(${it.planetKitStartFailReason})"
                    }
                    showStartFailedDialog(contents)
                }
            }
        }
        return binding.root
    }

    private fun makeCall(isVideoCall: Boolean) {
        val peerId = binding.etPeerId.text.toString()
        if (peerId.isEmpty()) {
            val title = this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_common_error_startfail0)
            val buttonText = this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_setting_popup3)
            val contents = this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_common_error_startfail1)
            SingleButtonFragmentDialog(title, contents, buttonText) {
            }.show(childFragmentManager, null)
            return
        }
        lifecycleScope.launch {
            oneOnOneMakeCallViewModel.makeCall(peerId, isVideoCall)
        }
    }

    private fun showStartFailedDialog(contents: String) {
        val title = this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_common_error_startfail0)
        val buttonText = this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_setting_popup3)
        SingleButtonFragmentDialog(title, contents, buttonText) {
            binding.root.findNavController().navigateUp()
        }.show(childFragmentManager, null)
    }
    private val appVersion: String
        get() = versionInfoViewModel.getAppVersion(requireContext())
    private val sdkVersion: String
        get() = versionInfoViewModel.getSDKVersion(requireContext())
}