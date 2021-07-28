package com.ytt.vmv.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ytt.vmv.databinding.FragmentViewKeyBinding
import com.ytt.vmv.models.ViewKeyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewKeyFragment : Fragment() {
    private val viewKeyViewModel: ViewKeyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentViewKeyBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = this@ViewKeyFragment
                viewModel = viewKeyViewModel
            }.root
    }
}