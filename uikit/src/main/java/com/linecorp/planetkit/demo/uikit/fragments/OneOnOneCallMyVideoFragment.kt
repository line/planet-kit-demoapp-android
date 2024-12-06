package com.linecorp.planetkit.demo.uikit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.linecorp.planetkit.demo.uikit.databinding.FragmentOneOnOneCallMyVideoBinding
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitVideoStatus
import com.linecorp.planetkit.demo.uikit.utils.observe
import com.linecorp.planetkit.demo.uikit.viewmodels.CameraViewModel
import com.linecorp.planetkit.demo.uikit.viewmodels.OneOnOneCallMyStatusViewModel

class OneOnOneCallMyVideoFragment(callInstanceId: Int): Fragment() {
    private val binding by lazy {
        FragmentOneOnOneCallMyVideoBinding.inflate(layoutInflater)
    }

    private val myStatusViewModel: OneOnOneCallMyStatusViewModel by viewModels {
        OneOnOneCallMyStatusViewModel.Factory(callInstanceId)
    }

    private val cameraViewModel: CameraViewModel by viewModels {
        CameraViewModel.Factory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        observe(cameraViewModel.isStarted) {
            if (it == true) {
                myStatusViewModel.resumeVideo()
            }
            else {
                myStatusViewModel.pauseVideo()
            }
        }

        observe(myStatusViewModel.videoStatus) {
            when(it) {
                UIKitVideoStatus.DISABLED, UIKitVideoStatus.PAUSED -> binding.videoView.isVisible = false
                UIKitVideoStatus.ENABLED -> binding.videoView.isVisible = true
                else -> {}
            }
            binding.imageNoVideo.isVisible = !binding.videoView.isVisible
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        myStatusViewModel.addMyVideoView(binding.videoView)
    }

    override fun onStop() {
        super.onStop()
        myStatusViewModel.removeVideoView(binding.videoView)
    }
}