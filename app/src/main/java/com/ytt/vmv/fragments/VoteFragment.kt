package com.ytt.vmv.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.volley.toolbox.StringRequest
import com.google.android.material.color.MaterialColors
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.ytt.vmv.R
import com.ytt.vmv.databinding.ListCardItemBinding
import com.ytt.vmv.models.VoteOptionModel
import com.ytt.vmv.models.VoteOptions
import com.ytt.vmv.network.NetworkSingleton

const val VOTE_URL = "http://10.0.2.2:3000/vote"

class VoteFragment : Fragment() {

    // TODO private val args: VoteFragmentArgs by navArgs()

    private val model: VoteOptions by viewModels()

    private var selectedOption = -1
    private val colorsList = mutableListOf<MutableLiveData<Int>>()

    private var defaultCardColor: Int = -1
    private var selectedCardColor: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_vote, container, false)

        defaultCardColor = CardView(requireContext()).cardBackgroundColor.defaultColor
        selectedCardColor = MaterialColors.getColor(
            requireContext(),
            R.attr.colorSecondary,
            ContextCompat.getColor(requireContext(), R.color.teal_200)
        )

        val optionsObserver = Observer<List<VoteOptionModel>> { newList ->
            val parent = view.findViewById<LinearLayout>(R.id.linear)

            parent.removeAllViews()

            newList.forEachIndexed { index, voteOption ->
                val itemBinding = ListCardItemBinding.inflate(layoutInflater)
                val cardColor = MutableLiveData(defaultCardColor)

                itemBinding.model =
                    VoteOptionModel(
                        voteOption.name,
                        voteOption.picUrl,
                        cardColor,
                    )

                itemBinding.card.setOnClickListener {
                    handleOnClick(index)
                }

                itemBinding.lifecycleOwner = this

                colorsList.add(cardColor)
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
            colorsList[selectedOption].value = defaultCardColor

        colorsList[i].value = selectedCardColor

        selectedOption = i
    }
}
