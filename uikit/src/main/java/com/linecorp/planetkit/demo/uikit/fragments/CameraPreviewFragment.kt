package com.linecorp.planetkit.demo.uikit.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.linecorp.planetkit.demo.uikit.databinding.FragmentCameraPreviewBinding
import com.linecorp.planetkit.demo.uikit.utils.observe
import com.linecorp.planetkit.demo.uikit.utils.observeNotNull
import com.linecorp.planetkit.demo.uikit.viewmodels.CameraViewModel

class CameraPreviewFragment: Fragment() {
    private val binding by lazy {
        FragmentCameraPreviewBinding.inflate(layoutInflater)
    }

    private val cameraViewModel: CameraViewModel by viewModels {
        CameraViewModel.Factory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        observeNotNull(cameraViewModel.isStarted) {
            binding.videoView.isVisible = it
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        cameraViewModel.addVideoView(binding.videoView)
    }

    override fun onDetach() {
        super.onDetach()
        cameraViewModel.removeVideoView(binding.videoView)
    }
}