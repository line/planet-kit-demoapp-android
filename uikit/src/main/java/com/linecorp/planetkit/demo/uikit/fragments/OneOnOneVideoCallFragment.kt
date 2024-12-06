package com.linecorp.planetkit.demo.uikit.fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.linecorp.planetkit.demo.uikit.R
import com.linecorp.planetkit.demo.uikit.databinding.FragmentOneOnOneVideoCallBinding
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitOneOnOneCallState
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitVideoStatus
import com.linecorp.planetkit.demo.uikit.utils.Permissions
import com.linecorp.planetkit.demo.uikit.utils.observe
import com.linecorp.planetkit.demo.uikit.viewmodels.CameraViewModel
import com.linecorp.planetkit.demo.uikit.viewmodels.OneOnOneCallInfoViewModel
import com.linecorp.planetkit.demo.uikit.viewmodels.OneOnOneCallMyStatusViewModel
import com.linecorp.planetkit.demo.uikit.viewmodels.OneOnOneCallStateViewModel

class OneOnOneVideoCallFragment(
    private val callInstanceId: Int,
    private val onEventListener: OnEventListener
): Fragment() {
    private val callInfoViewModel: OneOnOneCallInfoViewModel by viewModels {
        OneOnOneCallInfoViewModel.Factory(callInstanceId)
    }

    private val callStateViewModel: OneOnOneCallStateViewModel by viewModels {
        OneOnOneCallStateViewModel.Factory(callInstanceId)
    }

    private val callMyStatusViewModel: OneOnOneCallMyStatusViewModel by viewModels {
        OneOnOneCallMyStatusViewModel.Factory(callInstanceId)
    }

    private val cameraViewModel: CameraViewModel by viewModels {
        CameraViewModel.Factory
    }

    private val binding by lazy {
        FragmentOneOnOneVideoCallBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        disableBackPress()
        childFragmentManager.beginTransaction()
            .add(R.id.peer_video_container, OneOnOneCallPeerVideoFragment(callInstanceId))
            .add(R.id.my_video_container, OneOnOneCallMyVideoFragment(callInstanceId))
            .commit()

        if (!Permissions.hasCameraPermission(this.requireContext())) {
            callMyStatusViewModel.pauseVideo()
        } else {
            cameraViewModel.start()
        }

        initButtons()
        registerObservers()
        return binding.root
    }

    private fun initButtons() {
        binding.btnEndCall.setOnClickListener {
            callStateViewModel.endCall()
        }

        binding.imageMicMute.setOnClickListener {
            val muteOn = callMyStatusViewModel.isMuteOn.value == true
            callMyStatusViewModel.muteOn(!muteOn)
        }

        binding.imageCamera.setOnClickListener {
            if (cameraViewModel.isStarted.value == true) {
                cameraViewModel.stop()
            }
            else {
                if (!Permissions.hasCameraPermission(this.requireContext())) {
                    Permissions.requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.CAMERA))
                    return@setOnClickListener
                }
                cameraViewModel.start()
            }
        }

        binding.imageCameraType.setOnClickListener {
            val isFrontCamera = cameraViewModel.isFrontCamera.value == true
            cameraViewModel.setCameraType(!isFrontCamera)
        }
    }
    private fun registerObservers() {
        observe(callStateViewModel.callState) {
            if (it == UIKitOneOnOneCallState.DISCONNECTED) {
                onEventListener.onDisconnected(callStateViewModel.disconnectReason)
            }
        }

        observe(callInfoViewModel.durationString) {
            binding.tvCallDuration.text = it
        }

        observe(callMyStatusViewModel.isMuteOn) {
            val resourceId = if (it == true) {
                R.drawable.icon_mic_off_fill
            }
            else {
                R.drawable.icon_mic_on_fill
            }
            binding.imageMicMute.setImageResource(resourceId)
        }

        observe(cameraViewModel.isStarted) {
            val resourceId = if (it == true) {
                R.drawable.icon_camera_fill
            }
            else {
                R.drawable.icon_camera_fill_off
            }
            binding.imageCamera.setImageResource(resourceId)
        }
    }

    private var restoreVideoToResume: Boolean? = null

    override fun onPause() {
        super.onPause()
        callMyStatusViewModel.videoStatus.value?.let {
            restoreVideoToResume = it == UIKitVideoStatus.ENABLED
            callMyStatusViewModel.pauseVideo()
        }
    }

    override fun onResume() {
        super.onResume()
        restoreVideoToResume?.let {
            if (it) {
                callMyStatusViewModel.resumeVideo()
            }
            restoreVideoToResume = null
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

    fun interface OnEventListener {
        fun onDisconnected(reason: String)
    }
}