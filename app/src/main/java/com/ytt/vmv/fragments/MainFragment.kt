package com.ytt.vmv.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.ytt.vmv.R
import com.ytt.vmv.VMVApplication
import com.ytt.vmv.database.Election
import com.ytt.vmv.databinding.FragmentMainBinding
import com.ytt.vmv.databinding.ListTwoLinesItemBinding
import com.ytt.vmv.models.ElectionViewModel
import com.ytt.vmv.models.ElectionViewModelFactory
import java.math.BigInteger
import kotlin.random.Random

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

        binding.fab.apply {
            setOnClickListener {
                electionViewModel.insert(
                    Election(
                        "New election ${Random.nextInt(100)}", 0, 0,
                        BigInteger("1"),
                        BigInteger("2"),
                        BigInteger("3"),
                    )
                )
            }
        }

        setHasOptionsMenu(true)

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

        class ElectionViewHolder(private val binding: ListTwoLinesItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

            @SuppressLint("SetTextI18n")
            fun bind(election: Election, itemClickListener: ElectionItemClickListener) {
                binding.line1.text = election.name
                binding.line2.text = election.q.toString()
                binding.root.setOnClickListener {
                    itemClickListener.onItemClick(election)
                }
            }

            companion object {
                fun create(parent: ViewGroup): ElectionViewHolder {
                    val binding =
                        ListTwoLinesItemBinding.inflate(
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_view_keys -> {
                findNavController()
                    .navigate(MainFragmentDirections.actionMainFragmentToKeyListFragment())

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

fun interface ElectionItemClickListener {
    fun onItemClick(election: Election)
}
