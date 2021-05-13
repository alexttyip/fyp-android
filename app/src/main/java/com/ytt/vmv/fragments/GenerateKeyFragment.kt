package com.ytt.vmv.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ytt.vmv.VMVApplication
import com.ytt.vmv.cryptography.VoterKeyGenerator
import com.ytt.vmv.databinding.FragmentGenerateKeyBinding
import com.ytt.vmv.models.ElectionViewModel
import com.ytt.vmv.models.ElectionViewModelFactory
import kotlin.system.measureTimeMillis


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
        val (_, _, _, g, p, q) = election

        binding.btnG.setOnClickListener { showParamDialog("g", g.toString()) }
        binding.btnP.setOnClickListener { showParamDialog("p", p.toString()) }
        binding.btnQ.setOnClickListener { showParamDialog("q", q.toString()) }

        val overlay = binding.overlay

        binding.btnGenKey.setOnClickListener {
            val time = measureTimeMillis {

                overlay.visibility = View.VISIBLE

                val (trapdoorPrivate, trapdoorPublic, signingPrivate, signingPublic)
                        = VoterKeyGenerator.generate(g, p, q)

                election.trapdoorPublicKey = trapdoorPublic
                election.signingPublicKey = signingPublic

                // TODO securely save private keys

                electionViewModel.update(election)

                overlay.visibility = View.INVISIBLE
            }

            Toast.makeText(requireContext(), "Key generation took $time ms", Toast.LENGTH_LONG)
                .show()
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