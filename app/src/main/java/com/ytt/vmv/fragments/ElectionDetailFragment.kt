package com.ytt.vmv.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ytt.vmv.databinding.FragmentElectionDetailBinding
import com.ytt.vmv.fragments.ElectionDetailFragment.ViewKeysCallback
import com.ytt.vmv.fragments.ElectionDetailFragment.VoteCallback
import com.ytt.vmv.models.ElectionDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ElectionDetailFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private val electionDetailViewModel: ElectionDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentElectionDetailBinding.inflate(inflater, container, false)

        binding.apply {
            swipeRefresh.setOnRefreshListener(this@ElectionDetailFragment)

            viewKeysCallback = ViewKeysCallback {
                findNavController().navigate(electionDetailViewModel.getViewKeysDest())
            }

            voteCallback = VoteCallback {
                findNavController().navigate(electionDetailViewModel.getVoteDest())
            }

            viewModel = electionDetailViewModel
        }

        return binding.root
    }

    override fun onRefresh() {
        electionDetailViewModel.refresh()
    }

    fun interface ViewKeysCallback {
        fun view(electionName: String)
    }

    fun interface VoteCallback {
        fun vote(electionName: String)
    }
}
