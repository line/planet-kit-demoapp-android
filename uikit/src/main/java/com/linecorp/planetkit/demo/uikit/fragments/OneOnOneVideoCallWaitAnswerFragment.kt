package com.linecorp.planetkit.demo.uikit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.linecorp.planetkit.demo.uikit.databinding.FragmentOneOnOneVideoCallWaitAnswerBinding
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitOneOnOneCallState
import com.linecorp.planetkit.demo.uikit.utils.observe
import com.linecorp.planetkit.demo.uikit.viewmodels.CameraViewModel
import com.linecorp.planetkit.demo.uikit.viewmodels.OneOnOneCallInfoViewModel
import com.linecorp.planetkit.demo.uikit.viewmodels.OneOnOneCallMyStatusViewModel
import com.linecorp.planetkit.demo.uikit.viewmodels.OneOnOneCallStateViewModel

class OneOnOneVideoCallWaitAnswerFragment(
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

    private val cameraViewModel: CameraViewModel by viewModels {
        CameraViewModel.Factory
    }

    private val binding by lazy {
        FragmentOneOnOneVideoCallWaitAnswerBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        disableBackPress()
        binding.tvPeerId.text = callInfoViewModel.peerId
        myStatusViewModel.addMyVideoView(binding.myVideoView)

        binding.btnEndCall.setOnClickListener {
            callStateViewModel.endCall()
        }

        observe(callStateViewModel.callState) {
            if (it == UIKitOneOnOneCallState.CONNECTED) {
                onEventListener.onConnected()
            }
            else if (it == UIKitOneOnOneCallState.DISCONNECTED) {
                onEventListener.onDisconnected(callStateViewModel.disconnectReason)
            }
        }

        observe(cameraViewModel.isStarted) {
            if (it == true) {
                myStatusViewModel.resumeVideo()
            }
            else {
                myStatusViewModel.pauseVideo()
            }
        }
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        if (callStateViewModel.callState.value == UIKitOneOnOneCallState.WAIT_ANSWER) {
            cameraViewModel.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        if (callStateViewModel.callState.value == UIKitOneOnOneCallState.WAIT_ANSWER) {
            cameraViewModel.start()
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

    interface OnEventListener {
        fun onConnected()
        fun onDisconnected(reason: String)
    }
}