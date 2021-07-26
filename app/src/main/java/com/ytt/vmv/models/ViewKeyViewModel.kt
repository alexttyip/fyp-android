package com.ytt.vmv.models

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.ytt.vmv.cryptography.VoterKeyGenerator
import com.ytt.vmv.database.Election
import com.ytt.vmv.database.ElectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class ViewKeyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: ElectionRepository,
    @ApplicationContext applicationContext: Context,
) : ViewModel() {
    private val electionName: String =
        savedStateHandle.get<String>(ELECTION_NAME_SAVED_STATE_KEY) ?: ""

    val election: Election by lazy {
        runBlocking {
            repository.getByName(electionName).first().election
        }
    }

    val privateKeySignature: BigInteger by lazy {
        VoterKeyGenerator.getPrivateKey(
            applicationContext, election.name,
            VoterKeyGenerator.PrivateKey.SIGNATURE_PRIVATE_KEY
        )
    }

    val privateKeyTrapdoor: BigInteger by lazy {
        VoterKeyGenerator.getPrivateKey(
            applicationContext, election.name,
            VoterKeyGenerator.PrivateKey.TRAPDOOR_PRIVATE_KEY
        )
    }

    val dialogParams = MutableLiveData<ViewKeyDialog?>(null)

    fun onParamClick(paramName: String, value: BigInteger) {
        dialogParams.value = ViewKeyDialog(paramName, value)
    }

    companion object {
        const val ELECTION_NAME_SAVED_STATE_KEY = "election"
    }
}

data class ViewKeyDialog(val name: String, val value: BigInteger)