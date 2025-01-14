package com.linecorp.planetkit.demo.uikit.fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.linecorp.planetkit.demo.uikit.R
import com.linecorp.planetkit.demo.uikit.databinding.FragmentGroupCallPreviewBinding
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitGroupCallParam
import com.linecorp.planetkit.demo.uikit.utils.Permissions
import com.linecorp.planetkit.demo.uikit.utils.observe
import com.linecorp.planetkit.demo.uikit.viewmodels.CameraViewModel
import com.linecorp.planetkit.demo.uikit.viewmodels.GroupCallStateViewModel
import kotlinx.coroutines.launch

class GroupCallPreviewFragment(
    private val param: UIKitGroupCallParam,
    private val onClose: (byUnhandledException: Boolean) -> Unit
): Fragment() {
    private var isCameraStarted = true

    private val binding by lazy {
        FragmentGroupCallPreviewBinding.inflate(layoutInflater)
    }

    private val groupCallStateViewModel: GroupCallStateViewModel by viewModels {
        GroupCallStateViewModel.Factory
    }

    private val cameraViewModel: CameraViewModel by viewModels {
        CameraViewModel.Factory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cameraViewModel.setCameraType(true)
        if (Permissions.hasCameraPermission(this.requireContext())) {
            resumeCamera()
            isCameraStarted = true
        }
        else {
            pauseCamera()
            isCameraStarted = false
        }

        binding.tvRoomName.text = param.roomId

        childFragmentManager.beginTransaction()
            .add(R.id.preview_container, CameraPreviewFragment())
            .commit()

        binding.imageMicMute.setOnClickListener {
            if (param.muteOnStart == null || param.muteOnStart == false) {
                param.muteOnStart = true
                binding.imageMicMute.setImageResource(R.drawable.icon_mic_off_fill)
            } else {
                param.muteOnStart = false
                binding.imageMicMute.setImageResource(R.drawable.icon_mic_on_fill)
            }
        }

        binding.btnEnterRoom.setOnClickListener {
            param.isVideo = isCameraStarted
            cameraViewModel.stop()
            lifecycleScope.launch {
                groupCallStateViewModel.join(param)
            }
        }

        binding.imageCameraType.setOnClickListener {
            cameraViewModel.setCameraType(cameraViewModel.isFrontCamera.value != true)
        }

        binding.imageCamera.setOnClickListener {
            if (isCameraStarted) {
                pauseCamera()
                isCameraStarted = false
            } else {
                resumeCamera()
                isCameraStarted = true
            }
        }

        binding.btnClose.setOnClickListener {
            onClose(false)
        }

        observe(groupCallStateViewModel.joinResult) { result ->
            if (result?.isSuccessful == false) {
                val contents = if (result.planetKitStartFailReason != null) {
                    this.requireActivity().getString(R.string.lp_demoapp_common_default_error_msg) + "(${result.planetKitStartFailReason})"
                }
                else {
                    this.requireActivity().getString(R.string.lp_demoapp_common_default_error_msg) + "(${result.exceptionMsg})"
                }
                showJoinFailedDialog(contents)
            }
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (isCameraStarted) {
            resumeCamera()
        }
    }

    private fun showJoinFailedDialog(contents: String) {
        val title = this.requireActivity().getString(R.string.lp_demoapp_common_error_startfail0)
        val buttonText = this.requireActivity().getString(R.string.lp_demoapp_setting_popup3)
        SingleButtonFragmentDialog(title, contents, buttonText) {
            onClose(true)
        }.show(childFragmentManager, null)
    }

    private fun resumeCamera() {
        if (!Permissions.hasCameraPermission(this.requireContext())) {
            Permissions.requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.CAMERA))
            return
        }
        binding.imageCamera.setImageResource(R.drawable.icon_camera_fill)
        cameraViewModel.start()
    }

    private fun pauseCamera() {
        binding.imageCamera.setImageResource(R.drawable.icon_camera_fill_off)
        cameraViewModel.stop()
    }
}