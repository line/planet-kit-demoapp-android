package com.linecorp.planetkit.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.linecorp.planetkit.demo.databinding.FragmentOneOnOneCallFeaturesBinding

class OneOnOneCallFeaturesFragment: Fragment() {
    private val binding by lazy {
        FragmentOneOnOneCallFeaturesBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.btnPrev.setOnClickListener {
            it.findNavController().navigate(R.id.action_call_features_to_main)
        }

        binding.btnHome.setOnClickListener {
            it.findNavController().navigate(R.id.action_call_features_to_main)
        }

        binding.btnBasicCall.setOnClickListener {
            it.findNavController().navigate(R.id.action_call_features_to_make_call)
        }
        return binding.root
    }
}