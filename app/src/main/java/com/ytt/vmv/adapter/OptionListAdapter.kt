package com.ytt.vmv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.ytt.vmv.R
import com.ytt.vmv.database.ElectionOption
import com.ytt.vmv.databinding.ListCardItemBinding

class OptionListAdapter :
    ListAdapter<ElectionOption, OptionListAdapter.OptionViewHolder>(OptionDiffCallback()) {

    var selectedItem = -1
        private set
    private var defaultCardColor = -1
    private var selectedCardColor = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val context = parent.context

        defaultCardColor = CardView(context).cardBackgroundColor.defaultColor
        selectedCardColor = MaterialColors.getColor(
            context,
            R.attr.colorSecondary,
            ContextCompat.getColor(context, R.color.teal_200)
        )

        return OptionViewHolder(
            ListCardItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OptionViewHolder(
        private val binding: ListCardItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.card.setOnClickListener {
                notifyItemChanged(selectedItem)
                selectedItem = layoutPosition
                notifyItemChanged(selectedItem)
            }
        }

        fun bind(item: ElectionOption) {
            binding.apply {
                title.text = item.option

                card.setBackgroundColor(
                    if (selectedItem == layoutPosition) selectedCardColor else defaultCardColor
                )
            }
        }
    }

    class OptionDiffCallback : DiffUtil.ItemCallback<ElectionOption>() {
        override fun areItemsTheSame(oldItem: ElectionOption, newItem: ElectionOption): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ElectionOption, newItem: ElectionOption): Boolean {
            return oldItem == newItem
        }
    }
}

