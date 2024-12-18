package com.linecorp.planetkit.demo.uikit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.linecorp.planetkit.demo.uikit.databinding.FragmentOneOnOneVoiceCallBinding
import com.linecorp.planetkit.demo.uikit.utils.observe
import com.linecorp.planetkit.demo.uikit.utils.observeNotNull
import com.linecorp.planetkit.demo.uikit.viewmodels.OneOnOneCallInfoViewModel
import com.linecorp.planetkit.demo.uikit.viewmodels.OneOnOneCallMyStatusViewModel
import com.linecorp.planetkit.demo.uikit.viewmodels.OneOnOneCallStateViewModel
import com.linecorp.planetkit.demo.uikit.R
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitOneOnOneCallState
import com.linecorp.planetkit.demo.uikit.viewmodels.OneOnOneCallPeerControlViewModel

class OneOnOneVoiceCallFragment(
    callInstanceId: Int,
    private val onEventListener: OnEventListener
): Fragment() {
    private val callInfoViewModel: OneOnOneCallInfoViewModel by viewModels {
        OneOnOneCallInfoViewModel.Factory(callInstanceId)
    }

    private val callStateViewModel: OneOnOneCallStateViewModel by viewModels {
        OneOnOneCallStateViewModel.Factory(callInstanceId)
    }

    private val myStatusViewModel: OneOnOneCallMyStatusViewModel by viewModels {
        OneOnOneCallMyStatusViewModel.Factory(callInstanceId)
    }

    private val peerControlViewModel: OneOnOneCallPeerControlViewModel by viewModels {
        OneOnOneCallPeerControlViewModel.Factory(callInstanceId)
    }

    private val binding by lazy {
        FragmentOneOnOneVoiceCallBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        disableBackPress()
        initButtons()
        registerObservers()
        return binding.root
    }

    private fun initButtons() {
        binding.imageMicMute.setOnClickListener {
            if (myStatusViewModel.isMuteOn.value == true) {
                myStatusViewModel.muteOn(false)
            }
            else {
                myStatusViewModel.muteOn(true)
            }
        }

        binding.imageEndCall.setOnClickListener {
            callStateViewModel.endCall()
        }

        binding.tvPeerId.text = callInfoViewModel.peerId
    }

    fun interface OnEventListener {
        fun onDisconnected(reason: String)
    }
    private fun registerObservers() {
        observe(callStateViewModel.callState) {
            when(it) {
                UIKitOneOnOneCallState.IDLE,
                UIKitOneOnOneCallState.WAIT_ANSWER -> {
                    binding.groupCallConnected.visibility = View.GONE
                    binding.groupWaitAnswer.visibility = View.VISIBLE
                }
                UIKitOneOnOneCallState.CONNECTED -> {
                    binding.groupCallConnected.visibility = View.VISIBLE
                    binding.groupWaitAnswer.visibility = View.GONE
                }
                UIKitOneOnOneCallState.DISCONNECTED -> {
                    onEventListener.onDisconnected(callStateViewModel.disconnectReason)
                }
                null -> {}
            }
        }

        observeNotNull(callInfoViewModel.durationString) {
            binding.tvCallDuration.text = it
        }

        observe(myStatusViewModel.isMuteOn) {
            if (it == true) {
                binding.imageMicMute.setImageResource(R.drawable.icon_mic_off_fill)
            }
            else {
                binding.imageMicMute.setImageResource(R.drawable.icon_mic_on_fill)
            }
        }

        observeNotNull(peerControlViewModel.isMuteOn) {
            if (it) {
                binding.tvPeerMute.visibility = View.VISIBLE
            }
            else {
                binding.tvPeerMute.visibility = View.INVISIBLE
            }
        }

        observeNotNull(peerControlViewModel.isSpeaking) {
            binding.profile.imageSpeakingBackground.visibility =
                if (it) View.VISIBLE else View.INVISIBLE
        }
    }
    private fun disableBackPress() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Nothing to do.
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}