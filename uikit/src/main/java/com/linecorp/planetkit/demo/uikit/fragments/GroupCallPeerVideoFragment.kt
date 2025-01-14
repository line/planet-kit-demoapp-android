package com.linecorp.planetkit.demo.uikit.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.linecorp.planetkit.demo.uikit.databinding.FragmentGroupCallPeerVideoBinding
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitUser
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitVideoStatus
import com.linecorp.planetkit.demo.uikit.utils.observe
import com.linecorp.planetkit.demo.uikit.utils.observeNotNull
import com.linecorp.planetkit.demo.uikit.viewmodels.GroupCallPeerControlViewModel

class GroupCallPeerVideoFragment: Fragment() {
    private val binding by lazy {
        FragmentGroupCallPeerVideoBinding.inflate(layoutInflater)
    }

    private val peerControlViewModel: GroupCallPeerControlViewModel by viewModels {
        GroupCallPeerControlViewModel.Factory
    }

    private var isAttached = false
    private var hasPeer = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        observe(peerControlViewModel.isMuteOn) {
            binding.imageMute.isVisible = it == true
        }

        observe(peerControlViewModel.videoStatus) {
            if (it == null) {
                return@observe
            }
            binding.videoView.isVisible = it == UIKitVideoStatus.ENABLED
            binding.imageNoVideo.isVisible = !binding.videoView.isVisible
        }

        observeNotNull(peerControlViewModel.isSpeaking) {
            binding.imageSpeakingBackground.isVisible = it
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
        peerControlViewModel.startVideo()
    }

    override fun onDetach() {
        super.onDetach()
        isAttached = false
        peerControlViewModel.stopVideo()
    }

    fun setPeer(peer: UIKitUser) {
        hasPeer = true
        peerControlViewModel.setPeer(peer, binding.videoView)

        binding.tvPeerName.text = peer.displayName
        if (isAttached) {
            peerControlViewModel.startVideo()
        }
    }

    fun clearPeer() {
        hasPeer = false
        peerControlViewModel.clearPeer()

        binding.tvPeerName.text = null
        binding.videoView.resetFirstFrameRendered()
        binding.videoView.isVisible = false
    }
}