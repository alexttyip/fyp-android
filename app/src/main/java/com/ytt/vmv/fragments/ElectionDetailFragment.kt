package com.ytt.vmv.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ytt.vmv.database.Election
import com.ytt.vmv.databinding.FragmentElectionDetailBinding
import com.ytt.vmv.fragments.ElectionDetailFragment.ViewKeysCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ElectionDetailFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private val args: ElectionDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentElectionDetailBinding.inflate(inflater, container, false)

        val electionAndOptions = args.electionAndOptions
        val (election) = electionAndOptions

        binding.apply {
            swipeRefresh.setOnRefreshListener(this@ElectionDetailFragment)

            model = election

            viewKeysCallback = ViewKeysCallback {
                findNavController().navigate(
                    if (it.hasGeneratedKeyPairs())
                        ElectionDetailFragmentDirections.actionElectionDetailFragmentToViewKeyFragment(
                            election.name,
//                            election
                        )
                    else
                        ElectionDetailFragmentDirections.actionElectionDetailFragmentToGenerateKeyFragment(
                            election.name,
                            election
                        )
                )
            }
        }

        // TODO
        binding.btnVote.also {
            it.isEnabled =
                election.hasGeneratedKeyPairs() && electionAndOptions.hasElectionStarted()

            it.setOnClickListener {
                findNavController().navigate(
                    ElectionDetailFragmentDirections.actionElectionDetailFragmentToVoteFragment(
                        election.name,
                        args.electionAndOptions
                    )
                )
            }
        }

        return binding.root
    }

    override fun onRefresh() {
    }

    fun interface ViewKeysCallback {
        fun view(election: Election)
    }
}
