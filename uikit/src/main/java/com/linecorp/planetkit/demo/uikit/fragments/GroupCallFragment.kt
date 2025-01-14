package com.linecorp.planetkit.demo.uikit.fragments

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.linecorp.planetkit.demo.uikit.NotificationService
import com.linecorp.planetkit.demo.uikit.R
import com.linecorp.planetkit.demo.uikit.databinding.FragmentGroupCallBinding
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitVideoStatus
import com.linecorp.planetkit.demo.uikit.utils.Permissions
import com.linecorp.planetkit.demo.uikit.utils.observe
import com.linecorp.planetkit.demo.uikit.utils.observeNotNull
import com.linecorp.planetkit.demo.uikit.viewmodels.CameraViewModel
import com.linecorp.planetkit.demo.uikit.viewmodels.GroupCallInfoViewModel
import com.linecorp.planetkit.demo.uikit.viewmodels.GroupCallMyStatusViewModel
import com.linecorp.planetkit.demo.uikit.viewmodels.GroupCallPeerListViewModel
import com.linecorp.planetkit.demo.uikit.viewmodels.GroupCallStateViewModel

class GroupCallFragment: Fragment() {

    private val groupCallMyStatusViewModel: GroupCallMyStatusViewModel by viewModels {
        GroupCallMyStatusViewModel.Factory
    }

    private val groupCallStateViewModel: GroupCallStateViewModel by viewModels {
        GroupCallStateViewModel.Factory
    }

    private val groupCallPeerListViewModel: GroupCallPeerListViewModel by viewModels {
        GroupCallPeerListViewModel.Factory
    }

    private val groupCallInfoViewModel: GroupCallInfoViewModel by viewModels {
        GroupCallInfoViewModel.Factory
    }

    private val cameraViewModel: CameraViewModel by viewModels {
        CameraViewModel.Factory
    }

    private val binding by lazy {
        FragmentGroupCallBinding.inflate(layoutInflater)
    }

    private fun updateTitle(memberCount: Int) {
        binding.tvRoomTitle.text = groupCallInfoViewModel.roomName  + " ($memberCount)"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        childFragmentManager.beginTransaction()
            .add(R.id.container, GroupCallGridVideoFragment())
            .commit()

        observeNotNull(groupCallMyStatusViewModel.isMuteOn) {
            binding.imageMicMute.setImageResource(if(it) R.drawable.icon_mic_off else R.drawable.icon_mic_on)
        }

        observe(groupCallMyStatusViewModel.videoStatus) {
            when(it) {
                UIKitVideoStatus.DISABLED, UIKitVideoStatus.PAUSED -> binding.imageCamera.setImageResource(R.drawable.icon_camera_off)
                UIKitVideoStatus.ENABLED -> binding.imageCamera.setImageResource(R.drawable.icon_camera)
                else -> {}
            }
        }

        observe(groupCallPeerListViewModel.peerList) {
            if (it == null) {
                return@observe
            }
            updateTitle(it.count() + 1)
        }

        observe(groupCallPeerListViewModel.removedPeerList) {
            if (it.isNullOrEmpty()) {
                return@observe
            }
            context?.let {c ->
                val str = c.getString(R.string.lp_demoapp_group_scenarios_basic_inacall_toast).replace("{{User name}}",
                    it.last().displayName ?: it.last().id)
                Toast.makeText(c, str, Toast.LENGTH_SHORT).show()
            }
        }

        observeNotNull(groupCallInfoViewModel.durationString) {
            binding.tvCallDuration.text = it
        }

        binding.imageLeave.setOnClickListener {
            groupCallStateViewModel.leave()
        }

        binding.imageMicMute.setOnClickListener {
            val isMuteOn = groupCallMyStatusViewModel.isMuteOn.value
            groupCallMyStatusViewModel.muteOn(isMuteOn != true)
        }

        binding.imageCamera.setOnClickListener {
            when(groupCallMyStatusViewModel.videoStatus.value) {
                UIKitVideoStatus.PAUSED -> {
                    if (!Permissions.hasCameraPermission(this.requireContext())) {
                        Permissions.requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.CAMERA))
                        return@setOnClickListener
                    }
                    groupCallMyStatusViewModel.resumeVideo()
                }
                UIKitVideoStatus.ENABLED -> groupCallMyStatusViewModel.pauseVideo()
                else -> {}
            }
        }

        binding.imageCameraType.setOnClickListener {
            val isFrontCamera = cameraViewModel.isFrontCamera.value?:false
            cameraViewModel.setCameraType(!isFrontCamera)
        }

        updateTitle(1)

        if (!Permissions.hasCameraPermission(this.requireContext())) {
            groupCallMyStatusViewModel.pauseVideo()
        }
        showCallingNotification(requireContext())
        return binding.root
    }

    private fun showCallingNotification(context: Context) {
        NotificationService.showOngoingSession(
            context,
            context.getString(R.string.call_in_progress),
            true,
            Permissions.hasMicrophonePermission(context)
        )
    }
}