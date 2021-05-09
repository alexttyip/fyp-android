package com.ytt.vmv

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.ytt.vmv.databinding.FragmentVoteBinding
import com.ytt.vmv.databinding.ListCardItemBinding
import com.ytt.vmv.models.VoteOption
import com.ytt.vmv.models.VoteOptions

class VoteFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentVoteBinding.inflate(inflater, container, false)

        val voteOptions: VoteOptions by viewModels()

        binding.voteOptions = voteOptions

        binding.lifecycleOwner = this

        return binding.root
    }
}

@BindingAdapter("voteOptions")
fun setVoteOptions(parent: LinearLayout, options: List<VoteOption>?) {
    if (options == null) {
        val indicator = CircularProgressIndicator(parent.context)

        indicator.layoutParams = LinearLayout.LayoutParams(50, 50)
        indicator.isIndeterminate = true

        parent.addView(indicator)
        return
    }

    parent.removeAllViews()

    options.forEach {
        val itemBinding = ListCardItemBinding.inflate(
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
            parent,
            false
        )

        itemBinding.data = it
        parent.addView(itemBinding.root)
    }
}
