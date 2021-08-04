package com.ytt.vmv.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.ytt.vmv.Event
import com.ytt.vmv.cryptography.CipherText
import com.ytt.vmv.cryptography.Parameters
import com.ytt.vmv.cryptography.VoterKeyGenerator
import com.ytt.vmv.cryptography.elGamalDecrypt
import com.ytt.vmv.database.Election
import com.ytt.vmv.database.ElectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class VerifyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ElectionRepository,
    @ApplicationContext applicationContext: Context,
) : ViewModel() {
    val election = savedStateHandle.get<Election>("election")

    private val _trackerNumber = MutableLiveData<Event<String>>()

    val trackerNumber: LiveData<Event<String>>
        get() = _trackerNumber

    private val privateKeyTrapdoor by lazy {
        election?.let {
            VoterKeyGenerator.getPrivateKey(
                applicationContext, it.name,
                VoterKeyGenerator.PrivateKey.TRAPDOOR_PRIVATE_KEY
            )
        }
    }

    private val trackerNumbers by lazy {
        runBlocking {
            election?.let { repository.getTrackerNumbers(it.name) }
        }
    }

    fun onClickCalculate() {
        election?.let {
            val param = Parameters(
                it.g, it.p, it.q
            )

            val trackerNumberInGroup = elGamalDecrypt(
                param,
                privateKeyTrapdoor ?: return,
                CipherText(
                    it.alpha ?: return,
                    it.beta ?: return
                )
            )

            _trackerNumber.value = Event(
                trackerNumbers?.find { num -> num.trackerNumberInGroup == trackerNumberInGroup }
                    ?.let { tn -> "Your tracker number is ${tn.trackerNumber}." }
                    ?: "Cannot calculate tracker number."
            )
        }
    }
}