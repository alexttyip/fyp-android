package com.ytt.vmv.models

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.ytt.vmv.database.Election
import com.ytt.vmv.database.ElectionRepository
import com.ytt.vmv.fragments.ElectionDetailFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ElectionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: ElectionRepository,
) : ViewModel() {
    private val electionName: String =
        savedStateHandle.get<String>(ELECTION_NAME_SAVED_STATE_KEY) ?: ""

    val election: Election by lazy {
        runBlocking {
            repository.getByName(electionName).first().election
        }
    }

    val numTellers = election.numTellers
    val thresholdTellers = election.thresholdTellers

    fun getViewKeysText() = if (election.hasGeneratedKeyPairs()) "View Keys" else "Generate Keys"

    fun isVotingEnabled() = election.hasGeneratedKeyPairs()

    fun getViewKeysDest() = if (election.hasGeneratedKeyPairs())
        ElectionDetailFragmentDirections
            .actionElectionDetailFragmentToViewKeyFragment(
                election.name
            )
    else
        ElectionDetailFragmentDirections
            .actionElectionDetailFragmentToGenerateKeyFragment(
                election.name,
                election
            )

    fun getVoteDest() =
        ElectionDetailFragmentDirections
            .actionElectionDetailFragmentToVoteFragment(
                election.name
            )

    fun refresh() {

    }

    companion object {
        const val ELECTION_NAME_SAVED_STATE_KEY = "election"
    }
}
