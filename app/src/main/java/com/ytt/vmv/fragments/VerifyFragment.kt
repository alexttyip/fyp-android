package com.ytt.vmv.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.ytt.vmv.databinding.FragmentVerifyBinding
import com.ytt.vmv.models.VerifyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerifyFragment : Fragment() {
    private val verifyViewModel: VerifyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentVerifyBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@VerifyFragment
            viewModel = verifyViewModel
        }

        verifyViewModel.trackerNumber.observe(viewLifecycleOwner) { event ->
            val msg = event.getContentIfNotHandled() ?: return@observe

            Snackbar.make(
                binding.root,
                msg,
                Snackbar.LENGTH_LONG,
            ).show()
        }

        return binding.root
    }
}