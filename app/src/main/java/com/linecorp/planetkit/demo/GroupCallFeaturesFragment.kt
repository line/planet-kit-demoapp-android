package com.linecorp.planetkit.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.linecorp.planetkit.demo.databinding.FragmentGroupCallFeaturesBinding

class GroupCallFeaturesFragment(
    private val onEventListener: OnEventListener
): Fragment() {
    private val binding by lazy {
        FragmentGroupCallFeaturesBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.btnPrev.setOnClickListener {
//            it.findNavController().navigate(R.id.action_group_call_features_to_main)
            onEventListener.onPrevClicked()
        }

        binding.btnHome.setOnClickListener {
//            it.findNavController().navigate(R.id.action_group_call_features_to_main)
            onEventListener.onPrevClicked()
        }

        binding.btnBasicCall.setOnClickListener {
//            it.findNavController().navigate(R.id.action_group_call_features_to_join_group_call)
            onEventListener.onBasicCallClicked()
        }
        return binding.root
    }

    interface OnEventListener {
        fun onPrevClicked()
        fun onBasicCallClicked()
    }
}