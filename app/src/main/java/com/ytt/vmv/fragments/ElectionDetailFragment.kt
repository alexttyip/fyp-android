package com.ytt.vmv.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ytt.vmv.databinding.FragmentElectionDetailBinding
import com.ytt.vmv.fragments.ElectionDetailFragment.*
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
        val binding = FragmentElectionDetailBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = this@ElectionDetailFragment
                viewModel = electionDetailViewModel

                onClickViewKeys = OnClickViewKeys {
                    electionDetailViewModel.getViewKeysDest()
                        ?.let { findNavController().navigate(it) }
                }

                onClickUserParam = OnClickUserParam {
                    electionDetailViewModel.getUserParam()
                }

                onClickVote = OnClickVote {
                    electionDetailViewModel.vote()
                }
            }

        with(electionDetailViewModel) {
            userParamNav.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { findNavController().navigate(it) }
            }

            voteNav.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { findNavController().navigate(it) }
            }

            snackBarError.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let {
                    Snackbar.make(binding.root,
                        it,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

        return binding.root
    }

    fun interface OnClickViewKeys {
        fun click(electionName: String)
    }

    fun interface OnClickUserParam {
        fun click(electionName: String)
    }

    fun interface OnClickVote {
        fun click(electionName: String)
    }
}
