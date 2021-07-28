package com.ytt.vmv.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.ytt.vmv.adapter.OptionListAdapter
import com.ytt.vmv.databinding.FragmentVoteBinding
import com.ytt.vmv.models.VoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VoteFragment : Fragment() {
    private val voteViewModel: VoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentVoteBinding.inflate(inflater, container, false).apply {
            val optionListAdapter = OptionListAdapter()
            recycler.adapter = optionListAdapter

            fab.setOnClickListener {
                val selectedIdx = optionListAdapter.selectedItem

                if (selectedIdx != -1) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Vote")
                        .setMessage(voteViewModel.getConfirmDialogMessage(selectedIdx))
                        .setPositiveButton(android.R.string.ok,
                            voteViewModel.getDialogPositiveListener(selectedIdx))
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
                }
            }

            subscribeUi(optionListAdapter)
        }

        voteViewModel.snackbarMsg.observe(viewLifecycleOwner) { event ->
            val (code, msg) = event.getContentIfNotHandled() ?: return@observe

            Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()

            if (code == "OK") {
                TODO("navigate away")
            }
        }

        return binding.root
    }

    private fun subscribeUi(adapter: OptionListAdapter) {
        voteViewModel.electionWithOptions.observe(viewLifecycleOwner) {
            adapter.submitList(it.options)
        }
    }
}
