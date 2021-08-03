package com.ytt.vmv.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ytt.vmv.R
import com.ytt.vmv.adapter.ElectionListAdapter
import com.ytt.vmv.databinding.DialogJoinElectionBinding
import com.ytt.vmv.databinding.FragmentMainBinding
import com.ytt.vmv.models.ElectionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(), View.OnClickListener {
    private val electionViewModel: ElectionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        setHasOptionsMenu(true)

        return FragmentMainBinding.inflate(inflater, container, false)
            .apply {
                val adapter = ElectionListAdapter()

                recycler.also {
                    it.adapter = adapter

                    val decoration = DividerItemDecoration(context,
                        (it.layoutManager as LinearLayoutManager).orientation)
                    it.addItemDecoration(decoration)
                }

                swipe.setOnRefreshListener {
                    electionViewModel.updateFromRemote().invokeOnCompletion {
                        swipe.isRefreshing = false
                    }
                }

                fab.setOnClickListener(this@MainFragment)

                subscribeUi(adapter)
            }.root
    }

    override fun onClick(view: View) {
        val dialogBinding = DialogJoinElectionBinding
            .inflate(requireActivity().layoutInflater, null, false)

        AlertDialog.Builder(requireContext())
            .setTitle("Join Election")
            .setView(dialogBinding.root)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val input = dialogBinding.editText.text.toString().trim()
                electionViewModel.addElection(input,
                    { showResultSnackbar(view, "Election $input added") },
                    { showResultSnackbar(view, "Error: $it") }
                )

            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.hello -> {
            electionViewModel.addElection("hello",
                { showResultSnackbar(requireView(), "Election hello added") },
                { showResultSnackbar(requireView(), "Error: $it") }
            )
            true
        }
        R.id.clear -> {
            electionViewModel.clearAll()
            true
        }
        else -> false
    }

    private fun showResultSnackbar(view: View, text: String) {
        Snackbar.make(
            view,
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }


    private fun subscribeUi(adapter: ElectionListAdapter) {
        electionViewModel.allElections.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}
