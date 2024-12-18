package com.linecorp.planetkit.demo.uikit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.linecorp.planetkit.demo.uikit.databinding.FragmentOneOnOneCallPeerVideoBinding
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitVideoStatus
import com.linecorp.planetkit.demo.uikit.utils.observe
import com.linecorp.planetkit.demo.uikit.viewmodels.OneOnOneCallPeerControlViewModel

class OneOnOneCallPeerVideoFragment(callInstanceId: Int): Fragment() {
    private val binding by lazy {
        FragmentOneOnOneCallPeerVideoBinding.inflate(layoutInflater)
    }

    private val peerControlViewModel: OneOnOneCallPeerControlViewModel by viewModels {
        OneOnOneCallPeerControlViewModel.Factory(callInstanceId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        observe(peerControlViewModel.videoStatus) {
            if (it == null) {
                return@observe
            }
            binding.videoView.isVisible = it == UIKitVideoStatus.ENABLED
            binding.tvNoVideo.isVisible = !binding.videoView.isVisible
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        peerControlViewModel.addPeerVideoView(binding.videoView)
    }

    override fun onStop() {
        super.onStop()
        peerControlViewModel.removePeerVideoView(binding.videoView)
    }
}