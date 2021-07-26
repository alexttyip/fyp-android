package com.ytt.vmv.fragments

import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.color.MaterialColors
import com.ytt.vmv.R
import com.ytt.vmv.cryptography.EncryptAndSignVote
import com.ytt.vmv.cryptography.KeyPair
import com.ytt.vmv.cryptography.Parameters
import com.ytt.vmv.cryptography.VoterKeyGenerator
import com.ytt.vmv.database.Election
import com.ytt.vmv.database.ElectionOption
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VoteFragment : Fragment() {

    private val args: VoteFragmentArgs by navArgs()
    private val cards = mutableListOf<View>()
    private var selectedIdx = -1

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?,
//    ): View {
//        val binding = FragmentVoteBinding.inflate(inflater, container, false)
//
////        val (election, options) = args.electionAndOptions
//
//        val linear = binding.linear
//
//        options.forEachIndexed { i, option ->
//            val itemBinding = ListCardItemBinding.inflate(inflater, linear, false)
//
//            itemBinding.title.text = option.option
//            itemBinding.root.setOnClickListener {
//                changeColors(i)
//            }
//
//            cards.add(itemBinding.root)
//            linear.addView(itemBinding.root)
//        }
//
//        binding.fab.setOnClickListener {
//            if (selectedIdx != -1) {
//                val selected = options[selectedIdx]
//
//                val name = selected.option
//
//                AlertDialog.Builder(requireContext())
//                    .setTitle("Confirm Vote")
//                    .setMessage("You voted for: $name\n\nDo you wish to proceed?")
//                    .setPositiveButton(android.R.string.ok) { _, _ ->
//
//                        encryptVote(election, selected)
//                        // TODO encrypt
//                        /* val req = object : StringRequest(Method.POST, VOTE_URL, { response ->
//                            Log.e("Response", response)
//
//                            Snackbar.make(
//                                it!!,
//                                "You voted for $name",
//                                Snackbar.LENGTH_LONG
//                            )
//                                .setAction("Action", null).show()
//                        }, { error -> Log.e("Error", error.toString()) }) {
//                            override fun getParams(): MutableMap<String, String> {
//                                val map = HashMap<String, String>()
//
//                                map["vote"] = name
//
//                                return map
//                            }
//                        }
//
//                        NetworkSingleton.getInstance(requireContext()).addToRequestQueue(req) */
//                    }
//                    .setNegativeButton(android.R.string.cancel, null)
//                    .show()
//            }
//        }
//
//        return binding.root
//    }

    private fun changeColors(i: Int) {
        val defaultCardColor = CardView(requireContext()).cardBackgroundColor.defaultColor
        val selectedCardColor = MaterialColors.getColor(
            requireContext(),
            R.attr.colorSecondary,
            ContextCompat.getColor(requireContext(), R.color.teal_200)
        )

        cards.forEachIndexed { index, view ->
            view.setBackgroundColor(if (i == index) selectedCardColor else defaultCardColor)
        }
    }

    private fun encryptVote(election: Election, selected: ElectionOption) {
        val (electionName, _, _, g, p, q, electionPublicKey, publicKeySignature) = election
        val params = Parameters(g, p, q)

        val privateKeySignature =
            VoterKeyGenerator.getPrivateKey(
                requireActivity().applicationContext, electionName,
                VoterKeyGenerator.PrivateKey.SIGNATURE_PRIVATE_KEY
            )

        val (encryptedVote, proof) = EncryptAndSignVote.encryptVotes(
            params,
            electionPublicKey,
            KeyPair(privateKeySignature, publicKeySignature!!),
            selected.optionNumberInGroup
        )

    }
}
