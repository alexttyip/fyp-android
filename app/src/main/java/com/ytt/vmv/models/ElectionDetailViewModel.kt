package com.ytt.vmv.models

import android.util.Log
import androidx.lifecycle.*
import androidx.navigation.NavDirections
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.ytt.vmv.Event
import com.ytt.vmv.database.Election
import com.ytt.vmv.database.ElectionOption
import com.ytt.vmv.database.ElectionRepository
import com.ytt.vmv.fragments.ElectionDetailFragmentDirections
import com.ytt.vmv.network.NetworkSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class ElectionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ElectionRepository,
    private val network: NetworkSingleton,
) : ViewModel(), Response.Listener<JSONObject>, Response.ErrorListener {
    private val electionName =
        savedStateHandle.get<String>("electionName") ?: ""

    val election = repository.getOnlyElectionByName(electionName).asLiveData()

    private val _userParamNav = MutableLiveData<Event<NavDirections>>()
    private val _snackBarError = MutableLiveData<Event<String>>()

    val userParamNav: LiveData<Event<NavDirections>>
        get() = _userParamNav
    val snackBarError: LiveData<Event<String>>
        get() = _snackBarError

    fun getViewKeysDest() = election.value?.let {
        if (it.hasGeneratedKeyPairs())
            ElectionDetailFragmentDirections
                .actionElectionDetailFragmentToViewKeyFragment(it.name, it)
        else
            ElectionDetailFragmentDirections
                .actionElectionDetailFragmentToGenerateKeyFragment(it.name, it)
    }

    fun getUserParam() {
        election.value?.let {
            val param = JSONObject()
                .put("electionName", it.name)
                .put("deviceId", it.deviceId)

            // Get user param
            val req = JsonObjectRequest(
                Request.Method.POST,
                USER_PARAM_URL,
                param,
                this,
                this
            )

            network.addToRequestQueue(req)
        }
    }

    fun getVoteDest() = election.value?.let {
        ElectionDetailFragmentDirections
            .actionElectionDetailFragmentToVoteFragment(it.name)
    }

    private fun Election.getUserParamDest() = ElectionDetailFragmentDirections
        .actionElectionDetailFragmentToUserParamFragment(
            name,
            beta.toString(),
            encryptedTrackerNumberInGroup.toString(),
        )

    override fun onResponse(response: JSONObject?) {
        response ?: return

        val respBeta = BigInteger(response.getString("beta"))
        val respETNIG = response.getString("encryptedTrackerNumberInGroup")
        val voteOptions = response.getJSONArray("voteOptions")

        val electionOptions = (0 until voteOptions.length()).map { i ->
            voteOptions.getJSONObject(i).let {
                ElectionOption(
                    it.getString("option"),
                    BigInteger(it.getString("optionNumberInGroup")),
                    electionName
                )
            }
        }

        election.value?.apply {
            beta = respBeta
            encryptedTrackerNumberInGroup = respETNIG

            viewModelScope.launch { repository.update(this@apply) }
            viewModelScope.launch { repository.insertAll(electionOptions) }

            _userParamNav.value = Event(getUserParamDest())
        }
    }

    override fun onErrorResponse(error: VolleyError?) {
        error ?: return

        val code = try {
            val resp = JSONObject(String(error.networkResponse.data))
            Log.e("Resp", resp.toString())

            resp.getString("code")
        } catch (e: Exception) {
            error.networkResponse.data
        }

        val errorMsg = when (code) {
            UserParamErrors.ELECTION_NOT_STARTED.name -> "Election hasn't started."
            else -> "Server error."
        }

        _snackBarError.value = Event(errorMsg)
    }

    companion object {
        private const val USER_PARAM_URL = "https://snapfile.tech/voter/getVoterParamsAndOptions"

        enum class UserParamErrors {
            ELECTION_NOT_STARTED,
            NO_SUCH_VOTER,
            UNKNOWN
        }
    }
}
