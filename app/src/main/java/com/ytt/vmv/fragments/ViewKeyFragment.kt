package com.ytt.vmv.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.ytt.vmv.cryptography.VoterKeyGenerator
import com.ytt.vmv.cryptography.VoterKeyGenerator.Companion.getPrivateKey
import com.ytt.vmv.databinding.FragmentViewKeyBinding
import com.ytt.vmv.showParamDialog

class ViewKeyFragment : Fragment() {
    private val args: ViewKeyFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentViewKeyBinding.inflate(inflater, container, false)

        val election = args.election

        binding.btnG.setOnClickListener { showParamDialog(requireContext(), "param g", election.g) }
        binding.btnP.setOnClickListener { showParamDialog(requireContext(), "param p", election.p) }
        binding.btnQ.setOnClickListener { showParamDialog(requireContext(), "param q", election.q) }
        binding.btnSignaturePrivate.setOnClickListener {
            val signaturePrivateKey = getPrivateKey(
                requireActivity().applicationContext, election.name,
                VoterKeyGenerator.Companion.PrivateKey.SIGNATURE_PRIVATE_KEY
            )
            showParamDialog(requireContext(), "signature private key", signaturePrivateKey)
        }
        binding.btnSignaturePublic.setOnClickListener {
            showParamDialog(
                requireContext(),
                "signature public key",
                election.signingPublicKey!!
            )
        }
        binding.btnTrapdoorPrivate.setOnClickListener {
            val trapdoorPrivateKey = getPrivateKey(
                requireActivity().applicationContext, election.name,
                VoterKeyGenerator.Companion.PrivateKey.TRAPDOOR_PRIVATE_KEY
            )
            showParamDialog(requireContext(), "trapdoor private key", trapdoorPrivateKey)
        }
        binding.btnTrapdoorPublic.setOnClickListener {
            showParamDialog(
                requireContext(),
                "trapdoor public key",
                election.trapdoorPublicKey!!
            )
        }

        return binding.root
    }

}