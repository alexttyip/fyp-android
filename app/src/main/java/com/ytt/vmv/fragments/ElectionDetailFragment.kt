package com.ytt.vmv.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.ytt.vmv.databinding.FragmentElectionDetailBinding

class ElectionDetailFragment : Fragment() {
    private var electionDetailBinding: FragmentElectionDetailBinding? = null
    private val args: ElectionDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentElectionDetailBinding.inflate(inflater, container, false)
        electionDetailBinding = binding

        val election = args.election

        Log.e("Election", election.toString())

        binding.model = election

        binding.btnViewKeys.also {
            it.text =
                if (election.hasGeneratedKeyPairs())
                    "View Keys"
                else
                    "Generate Keys"

            it.setOnClickListener {
                // TODO
                Log.e("View keys", "OK")
            }
        }

        binding.btnVote.also {
            it.isEnabled = election.hasGeneratedKeyPairs()

            it.setOnClickListener {
                Log.e("Vote", "OK")
            }
        }

        return binding.root
    }
}