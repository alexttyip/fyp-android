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
import androidx.navigation.fragment.navArgs
import com.ytt.vmv.databinding.FragmentGenerateKeyBinding

class GenerateKeyFragment : Fragment() {
    private val args: GenerateKeyFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGenerateKeyBinding.inflate(inflater, container, false)

        binding.btnG.setOnClickListener { showParamDialog("g", args.g) }
        binding.btnP.setOnClickListener { showParamDialog("p", args.p) }
        binding.btnQ.setOnClickListener { showParamDialog("q", args.q) }

        binding.btnGenKey.setOnClickListener {
            // TODO
            Log.e("Gen keys", "OK")
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