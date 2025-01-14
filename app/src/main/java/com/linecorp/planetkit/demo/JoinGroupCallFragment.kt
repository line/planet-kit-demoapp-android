package com.linecorp.planetkit.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.linecorp.planetkit.demo.databinding.FragmentMakeGroupCallBinding
import com.linecorp.planetkit.demo.uikit.fragments.SingleButtonFragmentDialog

class JoinGroupCallFragment(
    private val listener: Listener
): Fragment() {
    private val binding by lazy {
        FragmentMakeGroupCallBinding.inflate(layoutInflater)
    }

    private val versionInfoViewModel: VersionInfoViewModel by viewModels {
        VersionInfoViewModel.Factory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.btnEnterPreview.setOnClickListener {
            if (binding.etRoomName.text.isNullOrEmpty()) {
                showErrorDialog()
                return@setOnClickListener
            }
            binding.etRoomName.text?.let {
                listener.onJoinGroupCall(it.toString())
            }
        }

        binding.btnPrev.setOnClickListener {
            listener.onPrev()
        }

        binding.btnClose.setOnClickListener {
            listener.onPrev()
        }

        binding.tvSdkVersion.text = sdkVersion
        binding.tvAppVersion.text = appVersion

        return binding.root
    }

    private fun showErrorDialog() {
        val title = this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_common_error_startfail0)
        val buttonText = this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_setting_popup3)
        val contents = this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_common_error_startfail2)
        SingleButtonFragmentDialog(title, contents, buttonText) {
        }.show(childFragmentManager, null)
    }

    interface Listener {
        fun onPrev()
        fun onJoinGroupCall(roomName: String)
    }

    private val appVersion: String
        get() = versionInfoViewModel.getAppVersion(requireContext())
    private val sdkVersion: String
        get() = versionInfoViewModel.getSDKVersion(requireContext())
}