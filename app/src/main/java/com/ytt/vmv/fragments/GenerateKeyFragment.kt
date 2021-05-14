package com.ytt.vmv.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.snackbar.Snackbar
import com.ytt.vmv.VMVApplication
import com.ytt.vmv.cryptography.VoterKeyGenerator
import com.ytt.vmv.databinding.FragmentGenerateKeyBinding
import com.ytt.vmv.models.ElectionViewModel
import com.ytt.vmv.models.ElectionViewModelFactory
import org.json.JSONObject


const val UPLOAD_KEYS_URL = "http://10.0.2.2:3000/uploadKeys"

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

        binding.btnG.setOnClickListener { showParamDialog("g", g.toString()) }
        binding.btnP.setOnClickListener { showParamDialog("p", p.toString()) }
        binding.btnQ.setOnClickListener { showParamDialog("q", q.toString()) }

        val overlay = binding.overlay

        binding.btnGenKey.setOnClickListener {
            overlay.visibility = View.VISIBLE

            val (trapdoorPublic, signingPublic) = VoterKeyGenerator.genAndStore(
                requireActivity().applicationContext, name, g, p, q
            )

            election.trapdoorPublicKey = trapdoorPublic
            election.signingPublicKey = signingPublic

            // Save public keys locally
            electionViewModel.update(election)

            // Upload public keys to backend
            val jsonObj = JSONObject()
                .put("election", name)
                .put("voterId", voterId)
                .put("publicKeyTrapdoor", trapdoorPublic.toString())
                .put("publicKeySignature", signingPublic.toString())

            val req =
                JsonObjectRequest(Request.Method.POST, UPLOAD_KEYS_URL, jsonObj, { response ->
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
                })

            (requireActivity().application as VMVApplication).network.addToRequestQueue(req)
        }

        return binding.root
    }

    private fun showParamDialog(paramName: String, param: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Content of param $paramName")
            .setMessage(param)
            .setNeutralButton(
                "Copy to Clipboard"
            ) { _, _ ->
                val clipboard =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                val clip = ClipData.newPlainText(paramName, param)

                clipboard.setPrimaryClip(clip)

                Toast.makeText(
                    requireContext(),
                    "Parameter $paramName copied to clipboard",
                    Toast.LENGTH_LONG
                ).show()
            }
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
}