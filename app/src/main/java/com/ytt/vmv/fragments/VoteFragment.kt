package com.ytt.vmv.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ytt.vmv.R
import com.ytt.vmv.databinding.ListCardItemBinding
import com.ytt.vmv.models.VoteOptionModel
import com.ytt.vmv.models.VoteOptions


class VoteFragment : Fragment() {

    private val model: VoteOptions by viewModels()

    private var selectedOption = -1
    private val selectedLst = mutableListOf<MutableLiveData<Boolean>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_vote, container, false)

        val optionsObserver = Observer<List<VoteOptionModel>> { newList ->
            val parent = view.findViewById<LinearLayout>(R.id.linear)

            parent.removeAllViews()

            newList.forEachIndexed { index, voteOption ->
                val itemBinding = ListCardItemBinding.inflate(layoutInflater)
                val selected = MutableLiveData(false)

                itemBinding.model =
                    VoteOptionModel(
                        voteOption.name,
                        voteOption.picUrl,
                        selected
                    )

                itemBinding.card.setOnClickListener {
                    handleOnClick(index)
                }

                itemBinding.lifecycleOwner = this

                selectedLst.add(selected)
                parent.addView(itemBinding.root)
            }
        }

        model.options.observe(viewLifecycleOwner, optionsObserver)

        return view
    }

    private fun handleOnClick(i: Int) {
        if (selectedOption != -1)
            selectedLst[selectedOption].value = false
        selectedLst[i].value = true

        selectedOption = i
    }
}
