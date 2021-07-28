package com.ytt.vmv.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ytt.vmv.databinding.FragmentUserParamBinding
import com.ytt.vmv.models.UserParamViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserParamFragment : Fragment() {
    private val userParamViewModel: UserParamViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentUserParamBinding.inflate(inflater, container, false).apply {
            viewModel = userParamViewModel
            lifecycleOwner = this@UserParamFragment
        }.root
    }
}