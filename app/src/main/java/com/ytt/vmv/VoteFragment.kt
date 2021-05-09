package com.ytt.vmv

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.ytt.vmv.databinding.FragmentVoteBinding
import com.ytt.vmv.databinding.ListCardItemBinding
import com.ytt.vmv.models.VoteOption

private val mockData = List(4) {
    VoteOption(
        "$it",
        "https://picsum.photos/id/${it * 10}/200"
    )
}

class VoteFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentVoteBinding.inflate(inflater, container, false)

        binding.voteOptions = mockData

        return binding.root
    }
}

@BindingAdapter("voteOptions")
fun setVoteOptions(parent: LinearLayout, options: List<VoteOption>) {
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
