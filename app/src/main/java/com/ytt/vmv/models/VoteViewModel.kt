package com.ytt.vmv.models

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import androidx.lifecycle.*
import com.android.volley.toolbox.StringRequest
import com.ytt.vmv.Event
import com.ytt.vmv.cryptography.*
import com.ytt.vmv.database.Election
import com.ytt.vmv.database.ElectionRepository
import com.ytt.vmv.network.NetworkSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class VoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: ElectionRepository,
    val network: NetworkSingleton,
    @ApplicationContext applicationContext: Context,
) : ViewModel() {
    val electionName = savedStateHandle.get<String>("electionName") ?: ""

    val electionWithOptions = repository.getByName(electionName).asLiveData()

    val privateKeySignature by lazy {
        VoterKeyGenerator.getPrivateKey(
            applicationContext, electionName,
            VoterKeyGenerator.PrivateKey.SIGNATURE_PRIVATE_KEY
        )
    }

    private val _snackbarMsg = MutableLiveData<Event<Pair<String, String>>>()

    val snackbarMsg: LiveData<Event<Pair<String, String>>>
        get() = _snackbarMsg

    fun getConfirmDialogMessage(selectedIdx: Int): String? {
        selectedIdx != -1 || return null

        return electionWithOptions.value?.let { (_, options) ->
            "You voted for: ${options[selectedIdx].option}\n\nDo you wish to proceed?"
        }
    }

    fun getDialogPositiveListener(selectedIdx: Int): DialogInterface.OnClickListener? {
        selectedIdx != -1 || return null

        return electionWithOptions.value?.let { (election, options) ->
            val selected = options[selectedIdx]

            DialogInterface.OnClickListener { _, _ ->
                // TODO use result
                val (cipherText, proof) = encryptVote(election, selected.optionNumberInGroup)

                val req = object : StringRequest(Method.POST, VOTE_URL, { response ->
                    Log.e("Response", response)

                    _snackbarMsg.value = Event("OK" to "You voted for ${selected.option}.")
                }, { error ->
                    Log.e("Error", error.toString())

                    _snackbarMsg.value = Event("Error" to "Server error.")
                }) {
                    override fun getParams(): MutableMap<String, String> {
                        val map = HashMap<String, String>()

                        // TODO Add parameters
//                        map["vote"] = name

                        return map
                    }
                }

                network.addToRequestQueue(req)
            }

        }
    }

    private fun encryptVote(
        election: Election,
        optionNumberInGroup: BigInteger,
    ): Pair<CipherText, EncryptProof> {
        val (_, _, _, g, p, q, electionPublicKey, publicKeySignature) = election
        val params = Parameters(g, p, q)

        return EncryptAndSignVote.encryptVotes(
            params,
            electionPublicKey,
            KeyPair(privateKeySignature, publicKeySignature!!),
            optionNumberInGroup
        )
    }

    companion object {
        const val VOTE_URL = "https://snapfile.tech/voter/vote"
    }
}