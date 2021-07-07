package com.ytt.vmv.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.snackbar.Snackbar
import com.ytt.vmv.VMVApplication
import com.ytt.vmv.database.Election
import com.ytt.vmv.database.ElectionOption
import com.ytt.vmv.databinding.FragmentMainBinding
import com.ytt.vmv.databinding.ListOneLineItemBinding
import com.ytt.vmv.models.ElectionViewModel
import com.ytt.vmv.models.ElectionViewModelFactory
import com.ytt.vmv.network.NetworkSingleton
import org.json.JSONObject
import java.math.BigInteger

const val PARAMS_URL = "https://snapfile.tech/voter/getElectionParams"

class MainFragment : Fragment(), ElectionItemClickListener {
    private var fragmentMainBinding: FragmentMainBinding? = null

    private val electionViewModel: ElectionViewModel by viewModels {
        ElectionViewModelFactory((requireActivity().application as VMVApplication).electionRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater, container, false)
        fragmentMainBinding = binding

        val adapter = ElectionListAdapter(this)
        val myLayoutManager = LinearLayoutManager(context)
        val decoration = DividerItemDecoration(context, myLayoutManager.orientation)

        binding.recycler.also {
            it.adapter = adapter
            it.layoutManager = myLayoutManager
            it.addItemDecoration(decoration)
        }

        electionViewModel.allElections.observe(viewLifecycleOwner) { elections ->
            elections?.let { adapter.submitList(it) }
        }

        binding.swipe.setOnRefreshListener {
            electionViewModel.updateFromRemote().invokeOnCompletion {
                binding.swipe.isRefreshing = false
            }
        }

        binding.fab.setOnClickListener {
            val editText = EditText(requireContext())
            editText.inputType = InputType.TYPE_CLASS_TEXT

            AlertDialog.Builder(requireContext())
                .setTitle("Join Election")
                .setView(editText)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val input = editText.text.toString().trim()
                    Log.e("Input", input)

                    val req = JsonObjectRequest(
                        Request.Method.GET,
                        "$PARAMS_URL/${input}",
                        null,
                        { response ->
                            Log.e("Response", response.toString())

                            saveElectionParams(input, response)

                            Snackbar.make(
                                it,
                                "Election $input added",
                                Snackbar.LENGTH_LONG
                            ).setAction("Action", null).show()
                        },
                        { error -> Log.e("Error", error.toString()) })

                    NetworkSingleton.getInstance(requireContext()).addToRequestQueue(req)
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        fragmentMainBinding = null
        super.onDestroyView()
    }

    private fun saveElectionParams(name: String, params: JSONObject) {
        // TODO voterId, numTellers, thresholdTellers
        val election = Election(
            name,
            1,
            4,
            3,
            BigInteger(params.getString("g")),
            BigInteger(params.getString("p")),
            BigInteger(params.getString("q")),
        )

        electionViewModel.insert(election)

        val optionsJson = params.getJSONArray("voteOptions")
        (0 until optionsJson.length()).forEach { i ->
            val obj = optionsJson.getJSONObject(i)

            val option = obj.getString("option")
            val optionNumberInGroup = obj.getString("optionNumberInGroup")

            electionViewModel.insert(
                ElectionOption(
                    option,
                    BigInteger(optionNumberInGroup),
                    name
                )
            )
        }
    }

    class ElectionListAdapter(
        private val itemClickListener: ElectionItemClickListener
    ) :
        ListAdapter<Election, ElectionListAdapter.ElectionViewHolder>(ElectionsComparator()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
            return ElectionViewHolder.create(parent)
        }

        override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
            holder.bind(getItem(position), itemClickListener)
        }

        class ElectionViewHolder(private val binding: ListOneLineItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

            @SuppressLint("SetTextI18n")
            fun bind(election: Election, itemClickListener: ElectionItemClickListener) {
                binding.line1.text = election.name
//                binding.line2.text = election.q.toString()
                binding.root.setOnClickListener {
                    itemClickListener.onItemClick(election)
                }
            }

            companion object {
                fun create(parent: ViewGroup): ElectionViewHolder {
                    val binding =
                        ListOneLineItemBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    return ElectionViewHolder(binding)
                }
            }
        }

        class ElectionsComparator : DiffUtil.ItemCallback<Election>() {
            override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }

    override fun onItemClick(election: Election) {
        findNavController()
            .navigate(
                MainFragmentDirections.actionMainFragmentToElectionDetailFragment(
                    election.name,
                    election
                )
            )
    }
}

fun interface ElectionItemClickListener {
    fun onItemClick(election: Election)
}
