package com.linecorp.planetkit.demo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.linecorp.planetkit.demo.databinding.FragmentSettingsBinding
import com.linecorp.planetkit.demo.uikit.LongPollingNotificationService
import com.linecorp.planetkit.demo.uikit.consts.StringSet
import com.linecorp.planetkit.demo.uikit.fragments.SingleButtonFragmentDialog
import com.linecorp.planetkit.demo.uikit.fragments.TwoButtonFragmentDialog
import com.linecorp.planetkit.demo.uikit.utils.observe
import java.text.SimpleDateFormat
import java.util.TimeZone

class SettingsFragment: Fragment() {
    private val binding by lazy {
        FragmentSettingsBinding.inflate(layoutInflater)
    }

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModel.Factory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.btnPrev.setOnClickListener {
            it.findNavController().navigate(R.id.action_settings_to_main)
        }

        binding.btnClose.setOnClickListener {
            it.findNavController().navigate(R.id.action_settings_to_main)
        }

        binding.btnSave.setOnClickListener {
            settingsViewModel.registerUserProfile(binding.etMyName.text.toString(), binding.etMyUserId.text.toString(), ServiceConstants.NOTIFICATION_TYPE)
        }

        binding.btnReset.setOnClickListener {
            showResetDialog()
        }

        binding.etMyName.setText(settingsViewModel.userName)
        binding.etMyUserId.setText(settingsViewModel.userId)

        registerObservers()
        return binding.root
    }

    private fun registerObservers() {
        observe(settingsViewModel.loading) {
            binding.progressBar.visibility = if (it == true) View.VISIBLE else View.INVISIBLE
        }

        observe(settingsViewModel.registered) {
            updateRegistrationStateButton(it == true)
        }

        observe(settingsViewModel.errorReason) {
            it?.let {
                showRegistrationFailedDialog(it)
            }
        }
    }
    private fun showResetDialog() {
        TwoButtonFragmentDialog(
            this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_setting_popup5),
            this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_setting_popup6),
            this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_setting_popup4),
            this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_setting_popup3),
            {},
            { settingsViewModel.resetUserProfile() }
        ).show(childFragmentManager, null)
    }

    private fun showRegistrationFailedDialog(errorReason: SettingsViewModel.ErrorReason) {
        val title = this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_setting_popup1)
        val buttonText = this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_setting_popup3)
        val contents = when(errorReason) {
            SettingsViewModel.ErrorReason.NOT_FOUND_USER_NAME_OR_USER_ID ->
                this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_setting_error_savefail)
            SettingsViewModel.ErrorReason.CONFLICT_USER_ID ->
                this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_setting_popup2)
            else ->  // to do implements
                this.requireActivity().getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_setting_popup1)
        }

        SingleButtonFragmentDialog(title, contents, buttonText) {
        }.show(childFragmentManager, null)
    }

    private fun updateRegistrationStateButton(registered: Boolean) {
        if (registered) {
            binding.btnReset.visibility = View.VISIBLE
            binding.btnSave.visibility = View.GONE
            binding.etMyName.isEnabled = false
            binding.etMyUserId.isEnabled = false

            val baseString = getString(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_setting_guide4)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            dateFormat.timeZone = TimeZone.getDefault()

            val formattedDate = settingsViewModel.expDate?.let {
                dateFormat.format(it) }
            val gmtInfo = dateFormat.timeZone.getDisplayName(false, TimeZone.SHORT)

            binding.tvDescriptionMyUserIdRestTime.text = formattedDate?.let {
                baseString
                    .replace("{{YYYY-MM-DD hh.mm.ss}}", it)
                    .replace("{{gmt info}}", gmtInfo)
            }
            binding.tvDescriptionMyUserIdRestTime.visibility = View.VISIBLE

            if (ServiceConstants.NOTIFICATION_TYPE == StringSet.NOTIFICATION_TYPE_LONG_POLLING) {
                context?.let {
                    val serviceIntent = Intent(it, LongPollingNotificationService::class.java)
                    ContextCompat.startForegroundService(it, serviceIntent)
                }
            }
        }
        else {
            binding.btnReset.visibility = View.GONE
            binding.btnSave.visibility = View.VISIBLE
            binding.etMyName.isEnabled = true
            binding.etMyUserId.isEnabled = true
            binding.etMyName.setText("")
            binding.etMyUserId.setText("")
            binding.tvDescriptionMyUserIdRestTime.visibility = View.GONE
        }
    }
}