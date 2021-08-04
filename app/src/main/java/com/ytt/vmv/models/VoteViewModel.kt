package com.ytt.vmv.models

import android.content.Context
import android.content.DialogInterface
import android.util.Base64
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
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class VoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ElectionRepository,
    private val network: NetworkSingleton,
    @ApplicationContext applicationContext: Context,
) : ViewModel() {
    val electionName = savedStateHandle.get<String>("electionName") ?: ""

    val electionWithOptions = repository.getByName(electionName).asLiveData()

    private val privateKeySignature by lazy {
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
                val (cipherText, proof) = encryptVote(election, selected.optionNumberInGroup)

                val bodyObj = JSONObject()
                    .put("electionName", electionName)
                    .put("beta", election.beta.toString())
                    .put("encryptedVote", cipherText.toByteArray().toBase64())
                    .put("encryptedVoteSignature", proof.encryptedVoteSignature.toBase64())
                    .put("encryptProof", proof.toJSONObject())

                val req = object : StringRequest(Method.POST, VOTE_URL, { response ->
                    Log.e("Response", response)

                    // Save that user has voted
                    viewModelScope.launch { repository.update(election.apply { hasVoted = true }) }

                    _snackbarMsg.value = Event("OK" to "You voted for ${selected.option}.")
                }, { error ->
                    _snackbarMsg.value = if (error.networkResponse == null) {
                        // No internet connection.
                        Event("Error" to "No internet connection.")
                    } else {
                        Log.e("Error", String(error.networkResponse.data))

                        Event("Error" to "Server error.")
                    }
                }) {
                    override fun getBody() = bodyObj.toString().toByteArray()

                    override fun getBodyContentType() = "application/json; charset=utf-8"
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

    private fun ByteArray.toBase64() = Base64.encodeToString(this, Base64.NO_WRAP)

    private fun EncryptProof.toJSONObject() = JSONObject()
        .put("c1R", c1R.toString())
        .put("c2R", c2R.toString())
        .put("c1Bar", c1Bar.toString())
        .put("c2Bar", c2Bar.toString())

    companion object {
        const val VOTE_URL = "https://snapfile.tech/voter/vote"
    }
}