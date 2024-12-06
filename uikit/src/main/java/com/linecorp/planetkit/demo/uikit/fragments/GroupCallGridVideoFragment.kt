package com.linecorp.planetkit.demo.uikit.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.linecorp.planetkit.demo.uikit.R
import com.linecorp.planetkit.demo.uikit.databinding.FragmentGroupCallGridVideoBinding
import com.linecorp.planetkit.demo.uikit.utils.observe
import com.linecorp.planetkit.demo.uikit.viewmodels.GroupCallPeerListViewModel

class GroupCallGridVideoFragment: Fragment() {
    private val binding by lazy {
        FragmentGroupCallGridVideoBinding.inflate(layoutInflater)
    }

    private val groupCallPeerListViewModel: GroupCallPeerListViewModel by viewModels {
        GroupCallPeerListViewModel.Factory
    }

    private val peerFragmentList = ArrayList<GroupCallPeerVideoFragment>()
    private val containerList = ArrayList<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        peerFragmentList.add(GroupCallPeerVideoFragment())
        peerFragmentList.add(GroupCallPeerVideoFragment())
        peerFragmentList.add(GroupCallPeerVideoFragment())
        peerFragmentList.add(GroupCallPeerVideoFragment())
        peerFragmentList.add(GroupCallPeerVideoFragment())

        containerList.add(R.id.grid_3_by_2_container_2)
        containerList.add(R.id.grid_3_by_2_container_3)
        containerList.add(R.id.grid_3_by_2_container_4)
        containerList.add(R.id.grid_3_by_2_container_5)
        containerList.add(R.id.grid_3_by_2_container_6)

        observe(groupCallPeerListViewModel.peerList) {
            if (it == null) {
                return@observe
            }

            for (i in 0 until peerFragmentList.size) {
                val transaction = childFragmentManager.beginTransaction()
                if (it.size > i) {
                    peerFragmentList[i].setPeer(it[i])
                    transaction.show(peerFragmentList[i])
                } else {
                    peerFragmentList[i].clearPeer()
                    transaction.hide(peerFragmentList[i])
                }
                transaction.commit()
            }
        }

        childFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .add(R.id.grid_3_by_2_container_1, GroupCallMyVideoFragment())
            .add(R.id.grid_3_by_2_container_2, peerFragmentList[0])
            .add(R.id.grid_3_by_2_container_3, peerFragmentList[1])
            .add(R.id.grid_3_by_2_container_4, peerFragmentList[2])
            .add(R.id.grid_3_by_2_container_5, peerFragmentList[3])
            .add(R.id.grid_3_by_2_container_6, peerFragmentList[4])
            .commit()

        return binding.root
    }
}