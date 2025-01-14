package com.linecorp.planetkit.demo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.linecorp.planetkit.demo.databinding.FragmentMainBinding
import com.linecorp.planetkit.demo.uikit.LongPollingNotificationService
import com.linecorp.planetkit.demo.uikit.consts.StringSet

class MainFragment: Fragment() {
    private val binding by lazy {
        FragmentMainBinding.inflate(layoutInflater)
    }

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModel.Factory
    }

    private val versionInfoViewModel: VersionInfoViewModel by viewModels {
        VersionInfoViewModel.Factory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.btnSetting.setOnClickListener {
            it.findNavController().navigate(R.id.action_main_to_settings_fragment)
        }

        binding.btnCall.setOnClickListener {
            it.findNavController().navigate(R.id.action_main_to_call_features_fragment)
        }

        binding.btnGroupCall.setOnClickListener {
            it.findNavController().navigate(R.id.action_main_to_group_call_first_fragment)
        }

        binding.tvSdkVersion.text = sdkVersion
        binding.tvAppVersion.text = appVersion

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (settingsViewModel.isRegistered) {
            binding.tvDescription.visibility = View.GONE
            binding.settingRedDot.visibility = View.GONE
            binding.btnCall.isEnabled = true
            binding.btnGroupCall.isEnabled = true

            if (ServiceConstants.NOTIFICATION_TYPE == StringSet.NOTIFICATION_TYPE_LONG_POLLING) {
                context?.let {
                    val serviceIntent = Intent(it, LongPollingNotificationService::class.java)
                    startForegroundService(it, serviceIntent)
                }
            }
        }
        else {
            binding.tvDescription.visibility = View.VISIBLE
            binding.settingRedDot.visibility = View.VISIBLE
            binding.btnCall.isEnabled = false
            binding.btnGroupCall.isEnabled = false
        }
    }

    private val appVersion: String
        get() = versionInfoViewModel.getAppVersion(requireContext())
    private val sdkVersion: String
        get() = versionInfoViewModel.getSDKVersion(requireContext())
}