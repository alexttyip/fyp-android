package com.ytt.vmv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ytt.vmv.database.Election
import com.ytt.vmv.databinding.ListOneLineItemBinding
import com.ytt.vmv.fragments.MainFragmentDirections

class ElectionListAdapter :
    ListAdapter<Election, ElectionListAdapter.ElectionViewHolder>(ElectionDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder(
            ListOneLineItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ElectionViewHolder(
        private val binding: ListOneLineItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setOnClickListener {
                binding.election?.let { election ->
                    it.findNavController()
                        .navigate(
                            MainFragmentDirections.actionMainFragmentToElectionDetailFragment(
                                election.name
                            )
                        )
                }
            }
        }

        fun bind(item: Election) {
            binding.apply {
                election = item
                executePendingBindings()
            }
        }
    }

    class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
        override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem == newItem
        }
    }
}
