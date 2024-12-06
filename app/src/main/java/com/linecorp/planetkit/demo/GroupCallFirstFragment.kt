package com.linecorp.planetkit.demo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.linecorp.planetkit.demo.databinding.FragmentGroupCallFirstBinding
import com.linecorp.planetkit.demo.uikit.GroupCallActivity
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitGroupCallParam

class GroupCallFirstFragment: Fragment() {
    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when(it.resultCode) {
            GroupCallActivity.ACTIVITY_RESULT_NO_PARAM -> {

            }
            GroupCallActivity.ACTIVITY_RESULT_END -> {

            }
            GroupCallActivity.ACTIVITY_RESULT_UNHANDLED_EXCEPTION -> {
                binding.root.findNavController().navigateUp()
            }
        }
    }

    private val binding by lazy {
        FragmentGroupCallFirstBinding.inflate(layoutInflater)
    }

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModel.Factory
    }

    private fun replaceJoinGroupCallFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.container, JoinGroupCallFragment(object: JoinGroupCallFragment.Listener{
                override fun onPrev() {
                    replaceFeaturesFragment()
                }

                override fun onJoinGroupCall(roomName: String) {
                    val intent = Intent(context, GroupCallActivity::class.java)
                    intent.putExtra(GroupCallActivity.UIKIT_GROUP_CALL_PARAM, UIKitGroupCallParam(
                        myId = settingsViewModel.userId,
                        myDisplayName = settingsViewModel.userName,
                        myServiceId = ServiceConstants.SERVICE_ID,
                        roomId = roomName,
                        roomServiceId = ServiceConstants.SERVICE_ID,
                        isVideo = true
                    ))
                    activityLauncher.launch(intent)
                }

            }))
            .commit()
    }

    private fun replaceFeaturesFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.container, GroupCallFeaturesFragment(object: GroupCallFeaturesFragment.OnEventListener{
                override fun onPrevClicked() {
                    binding.root.findNavController().navigateUp()
                }

                override fun onBasicCallClicked() {
                    replaceJoinGroupCallFragment()
                }

            }))
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        replaceFeaturesFragment()

        return binding.root
    }
}