package com.ytt.vmv.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.volley.toolbox.StringRequest
import com.google.android.material.snackbar.Snackbar
import com.ytt.vmv.VMVApplication
import com.ytt.vmv.cryptography.VoterKeyGenerator
import com.ytt.vmv.databinding.FragmentGenerateKeyBinding
import com.ytt.vmv.models.ElectionViewModel
import com.ytt.vmv.models.ElectionViewModelFactory
import com.ytt.vmv.showParamDialog
import org.json.JSONObject


const val UPLOAD_KEYS_URL = "https://snapfile.tech/voter/uploadKeys"

class GenerateKeyFragment : Fragment() {
    private val args: GenerateKeyFragmentArgs by navArgs()

    private val electionViewModel: ElectionViewModel by viewModels {
        ElectionViewModelFactory((requireActivity().application as VMVApplication).electionRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGenerateKeyBinding.inflate(inflater, container, false)

        val election = args.election
        val (name, voterId, _, _, g, p, q) = election

        binding.btnG.setOnClickListener { showParamDialog(requireContext(), "param g", election.g) }
        binding.btnP.setOnClickListener { showParamDialog(requireContext(), "param p", election.p) }
        binding.btnQ.setOnClickListener { showParamDialog(requireContext(), "param q", election.q) }

        val overlay = binding.overlay

        binding.btnGenKey.setOnClickListener {
            overlay.visibility = View.VISIBLE

            val (signingPublic, trapdoorPublic) = VoterKeyGenerator.genAndStore(
                requireActivity().applicationContext, name, g, p, q
            )

            election.publicKeySignature = signingPublic
            election.publicKeyTrapdoor = trapdoorPublic

            // Save public keys locally
            electionViewModel.update(election)

            // Upload public keys to backend
            val req =
                object : StringRequest(Method.POST, UPLOAD_KEYS_URL, { response ->
                    Log.e("Response", response.toString())

                    overlay.visibility = View.INVISIBLE

                    Snackbar.make(
                        container!!,
                        "Keys generated successfully",
                        Snackbar.LENGTH_LONG
                    ).show()

                    findNavController().navigateUp()
                }, { error ->
                    Log.e("Error", error.toString())

                    Snackbar.make(
                        container!!,
                        "Server error",
                        Snackbar.LENGTH_LONG
                    ).show()

                    overlay.visibility = View.INVISIBLE
                }) {
                    override fun getBody(): ByteArray {
                        val jsonObj = JSONObject()
                            .put("electionName", name)
                            .put("voterId", voterId)
                            .put("publicKeySignature", signingPublic.toString())
                            .put("publicKeyTrapdoor", trapdoorPublic.toString())

                        return jsonObj.toString().toByteArray()
                    }

                    override fun getBodyContentType() = "application/json; charset=utf-8"
                }

            (requireActivity().application as VMVApplication).network.addToRequestQueue(req)
        }

        return binding.root
    }
}