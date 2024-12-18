package com.linecorp.planetkit.demo.uikit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.linecorp.planetkit.demo.uikit.databinding.FragmentOneOnOneIncomingCallBinding
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitOneOnOneCallState
import com.linecorp.planetkit.demo.uikit.utils.observe
import com.linecorp.planetkit.demo.uikit.viewmodels.OneOnOneCallInfoViewModel
import com.linecorp.planetkit.demo.uikit.viewmodels.OneOnOneCallStateViewModel

class OneOnOneIncomingCallFragment(
    private val callInstanceId: Int, private val onEventListener: OnEventListener
): Fragment() {
    private val callInfoViewModel: OneOnOneCallInfoViewModel by viewModels {
        OneOnOneCallInfoViewModel.Factory(callInstanceId)
    }

    private val callStateViewModel: OneOnOneCallStateViewModel by viewModels {
        OneOnOneCallStateViewModel.Factory(callInstanceId)
    }

    private val binding by lazy {
        FragmentOneOnOneIncomingCallBinding.inflate(layoutInflater)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        disableBackPress()
        initTextView()

        binding.imageEndCall.setOnClickListener {
            callStateViewModel.endCall()
        }

        binding.imageAcceptCall.setOnClickListener {
            callStateViewModel.acceptCall()
        }

        observe(callStateViewModel.callState) {
            if (it == UIKitOneOnOneCallState.CONNECTED) {
                onEventListener.onConnected(callInfoViewModel.isVoiceCall)
            }
            else if (it == UIKitOneOnOneCallState.DISCONNECTED) {
                onEventListener.onDisconnected(callStateViewModel.disconnectReason)
            }
        }

        return binding.root
    }

    private fun disableBackPress() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Nothing to do.
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
    interface OnEventListener {
        fun onConnected(isVoiceCall: Boolean)
        fun onDisconnected(reason: String)
    }

    private fun initTextView() {
        binding.tvCallerId.text = callInfoViewModel.peerId

        val callType = if (callInfoViewModel.isVoiceCall) "voice" else "video"
        binding.tvIncomingCallType.text = "Incoming $callType call"
    }
}