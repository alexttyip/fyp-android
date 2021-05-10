package com.ytt.vmv.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.volley.toolbox.StringRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.ytt.vmv.R
import com.ytt.vmv.databinding.ListCardItemBinding
import com.ytt.vmv.models.VoteOptionModel
import com.ytt.vmv.models.VoteOptions
import com.ytt.vmv.network.NetworkSingleton

const val VOTE_URL = "http://10.0.2.2:3000/vote"

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            if (selectedOption != -1) {
                val name = model.options.value?.get(selectedOption)?.name!!

                AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Vote")
                    .setMessage("You voted for: $name\n\nDo you wish to proceed?")
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        val req = object : StringRequest(Method.POST, VOTE_URL, { response ->
                            Log.e("Response", response)

                            Snackbar.make(
                                it!!,
                                "You voted for $name",
                                Snackbar.LENGTH_LONG
                            )
                                .setAction("Action", null).show()
                        }, { error -> Log.e("Error", error.toString()) }) {
                            override fun getParams(): MutableMap<String, String> {
                                val map = HashMap<String, String>()

                                map["vote"] = name

                                return map
                            }
                        }

                        NetworkSingleton.getInstance(requireContext()).addToRequestQueue(req)


                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            }
        }
    }

    private fun handleOnClick(i: Int) {
        if (selectedOption != -1)
            selectedLst[selectedOption].value = false
        selectedLst[i].value = true

        selectedOption = i
    }
}
