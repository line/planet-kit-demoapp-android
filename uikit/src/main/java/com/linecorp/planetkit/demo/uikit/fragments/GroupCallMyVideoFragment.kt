package com.linecorp.planetkit.demo.uikit.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.linecorp.planetkit.demo.uikit.databinding.FragmentGroupCallMyVideoBinding
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitVideoStatus
import com.linecorp.planetkit.demo.uikit.utils.observe
import com.linecorp.planetkit.demo.uikit.utils.observeNotNull
import com.linecorp.planetkit.demo.uikit.viewmodels.GroupCallMyStatusViewModel

class GroupCallMyVideoFragment: Fragment() {
    private val binding by lazy {
        FragmentGroupCallMyVideoBinding.inflate(layoutInflater)
    }

    private val groupCallMyStatusViewModel: GroupCallMyStatusViewModel by viewModels {
        GroupCallMyStatusViewModel.Factory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.tvMyName.text = groupCallMyStatusViewModel.me?.displayName?: groupCallMyStatusViewModel.me?.id

        observe(groupCallMyStatusViewModel.videoStatus) {
            when(it) {
                UIKitVideoStatus.DISABLED, UIKitVideoStatus.PAUSED -> binding.videoView.isVisible = false
                UIKitVideoStatus.ENABLED -> binding.videoView.isVisible = true
                else -> {}
            }
            binding.imageNoVideo.isVisible = !binding.videoView.isVisible
        }

        observeNotNull(groupCallMyStatusViewModel.isMuteOn) {
            binding.imageMute.visibility = if (it) View.VISIBLE else View.GONE
        }

        observeNotNull(groupCallMyStatusViewModel.isSpeaking) {
            binding.imageSpeakingBackground.visibility = if (it) View.VISIBLE else View.GONE
        }

        return binding.root
    }

    private var restoreVideoToResume: Boolean? = null

    override fun onPause() {
        super.onPause()
        groupCallMyStatusViewModel.videoStatus.value?.let {
            restoreVideoToResume = it == UIKitVideoStatus.ENABLED
            groupCallMyStatusViewModel.pauseVideo()
        }
    }

    override fun onResume() {
        super.onResume()
        restoreVideoToResume?.let {
            if (it) {
                groupCallMyStatusViewModel.resumeVideo()
            }
            restoreVideoToResume = null
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        groupCallMyStatusViewModel.addVideo(binding.videoView)
    }

    override fun onDetach() {
        super.onDetach()
        groupCallMyStatusViewModel.removeVideo(binding.videoView)
    }
}