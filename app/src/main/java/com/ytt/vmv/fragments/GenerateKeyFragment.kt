package com.ytt.vmv.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ytt.vmv.databinding.FragmentGenerateKeyBinding
import com.ytt.vmv.models.GenerateKeyViewModel
import com.ytt.vmv.showParamDialog
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigInteger

const val UPLOAD_KEYS_URL = "https://snapfile.tech/voter/uploadKeys"

@AndroidEntryPoint
class GenerateKeyFragment : Fragment() {
    private val genKeyViewModel: GenerateKeyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentGenerateKeyBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = this@GenerateKeyFragment

                viewModel = genKeyViewModel.also {
                    it.uploadResp.observe(viewLifecycleOwner) { resp ->
                        if (resp == null) return@observe

                        Snackbar.make(
                            container!!,
                            "Keys generated successfully",
                            Snackbar.LENGTH_LONG
                        ).show()

                        findNavController().navigateUp()
                    }

                    it.uploadError.observe(viewLifecycleOwner) { error ->
                        if (error == null) return@observe

                        Snackbar.make(
                            container!!,
                            "Server error",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }

                setParamDialogCallback { paramName, value ->
                    showParamDialog(requireContext(), paramName, value)
                }
            }.root
    }

    fun interface ParamDialogCallback {
        fun view(paramName: String, value: BigInteger)
    }
}