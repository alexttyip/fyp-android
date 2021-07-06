package com.ytt.vmv.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.ytt.vmv.VMVApplication
import com.ytt.vmv.database.Election
import com.ytt.vmv.databinding.FragmentMainBinding
import com.ytt.vmv.databinding.ListOneLineItemBinding
import com.ytt.vmv.models.ElectionViewModel
import com.ytt.vmv.models.ElectionViewModelFactory

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

//        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onDestroyView() {
        fragmentMainBinding = null
        super.onDestroyView()
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
