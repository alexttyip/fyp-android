package com.ytt.vmv.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ytt.vmv.databinding.FragmentElectionDetailBinding
import com.ytt.vmv.fragments.ElectionDetailFragment.ViewKeysCallback
import com.ytt.vmv.fragments.ElectionDetailFragment.VoteCallback
import com.ytt.vmv.models.ElectionDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ElectionDetailFragment : Fragment() {
    private val electionDetailViewModel: ElectionDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentElectionDetailBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = this@ElectionDetailFragment
                viewModel = electionDetailViewModel

                viewKeysCallback = ViewKeysCallback {
                    electionDetailViewModel.getViewKeysDest()
                        ?.let { findNavController().navigate(it) }
                }

                voteCallback = VoteCallback {
                    electionDetailViewModel.getVoteDest()?.let { findNavController().navigate(it) }
                }
            }.root
    }

    fun interface ViewKeysCallback {
        fun view(electionName: String)
    }

    fun interface VoteCallback {
        fun vote(electionName: String)
    }
}
