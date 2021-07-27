package com.ytt.vmv.models

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.navigation.NavDirections
import com.ytt.vmv.database.ElectionRepository
import com.ytt.vmv.fragments.ElectionDetailFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ElectionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: ElectionRepository,
) : ViewModel() {
    private val electionName: String =
        savedStateHandle.get<String>("election") ?: ""

    val election = repository.getOnlyElectionByName(electionName).asLiveData()

    fun getViewKeysDest(): NavDirections? = election.value?.let {
        if (it.hasGeneratedKeyPairs())
            ElectionDetailFragmentDirections
                .actionElectionDetailFragmentToViewKeyFragment(it.name)
        else
            ElectionDetailFragmentDirections
                .actionElectionDetailFragmentToGenerateKeyFragment(it.name)
    }

    fun getVoteDest(): NavDirections? = election.value?.let {
        ElectionDetailFragmentDirections
            .actionElectionDetailFragmentToVoteFragment(it.name)
    }
}
