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
import com.ytt.vmv.database.ElectionTrackerNumber
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
) : ViewModel() {
    private val electionName =
        savedStateHandle.get<String>("electionName") ?: ""

    val election = repository.getOnlyElectionByName(electionName).asLiveData()

    private val _userParamNav = MutableLiveData<Event<NavDirections>>()
    private val _voteNav = MutableLiveData<Event<NavDirections>>()
    private val _snackBarError = MutableLiveData<Event<String>>()

    val userParamNav: LiveData<Event<NavDirections>>
        get() = _userParamNav
    val voteNav: LiveData<Event<NavDirections>>
        get() = _voteNav
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
            if (it.hasObtainedUserParams()) {
                _userParamNav.value = Event(it.getUserParamDest())
                return
            }

            // Get user param
            UserParamRequest(it)
        }
    }

    fun vote() {
        election.value?.let {
            when {
                it.alpha != null -> _voteNav.value = Event(it.getVerifyDest())
                it.hasVoted -> AlphaAndTrackerNumberRequest(it)
                else -> {
                    _voteNav.value = Event(ElectionDetailFragmentDirections
                        .actionElectionDetailFragmentToVoteFragment(it.name))

                }
            }
        }
    }

    private fun Election.getUserParamDest() = ElectionDetailFragmentDirections
        .actionElectionDetailFragmentToUserParamFragment(
            name,
            beta.toString(),
            encryptedTrackerNumberInGroup.toString(),
        )

    private fun Election.getVerifyDest() = ElectionDetailFragmentDirections
        .actionElectionDetailFragmentToVerifyFragment(
            name,
            this
        )

    inner class UserParamRequest(private val election: Election) : Response.Listener<JSONObject>,
        Response.ErrorListener {
        init {
            val param = JSONObject()
                .put("electionName", election.name)
                .put("deviceId", election.deviceId)

            val req = JsonObjectRequest(
                Request.Method.POST,
                USER_PARAM_URL,
                param,
                this,
                this
            )

            network.addToRequestQueue(req)
        }

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

            election.apply {
                beta = respBeta
                encryptedTrackerNumberInGroup = respETNIG

                viewModelScope.launch { repository.update(this@apply) }
                viewModelScope.launch { repository.insertAllOptions(electionOptions) }

                _userParamNav.value = Event(getUserParamDest())
            }
        }

        override fun onErrorResponse(error: VolleyError) {
            // No network connection
            if (error.networkResponse == null) {
                _snackBarError.value = Event("No internet connection.")
                return
            }

            val code = try {
                val resp = JSONObject(String(error.networkResponse.data))
                Log.e("Resp", resp.toString())

                resp.getString("code")
            } catch (e: Exception) {
                UserParamErrors.UNKNOWN.name
            }

            val errorMsg = when (code) {
                UserParamErrors.ELECTION_NOT_STARTED.name -> "Election hasn't started."
                else -> "Server error."
            }

            _snackBarError.value = Event(errorMsg)
        }
    }

    inner class AlphaAndTrackerNumberRequest(private val election: Election) :
        Response.Listener<JSONObject>, Response.ErrorListener {
        init {
            val param = JSONObject()
                .put("electionName", election.name)
                .put("beta", election.beta.toString())

            val req = JsonObjectRequest(
                Request.Method.POST,
                USER_ALPHA_URL,
                param,
                this,
                this
            )

            network.addToRequestQueue(req)
        }

        override fun onResponse(response: JSONObject) {
            val alpha = response.getString("alpha")
            val tnsJson = response.getJSONArray("trackerNumbers")

            val tns = (0 until tnsJson.length()).map {
                val obj = tnsJson.getJSONObject(it)
                ElectionTrackerNumber(
                    BigInteger(obj.getString("trackerNumber")),
                    BigInteger(obj.getString("trackerNumberInGroup")),
                    obj.getString("encryptedTrackerNumberInGroup"),
                    electionName
                )
            }

            election.alpha = BigInteger(alpha)

            viewModelScope.launch { repository.update(election) }
            viewModelScope.launch { repository.insertAllTNs(tns) }

            _voteNav.value = Event(election.getVerifyDest())
        }

        override fun onErrorResponse(error: VolleyError) {
            // No network connection
            if (error.networkResponse == null) {
                _snackBarError.value = Event("No internet connection.")
                return
            }

            val code = try {
                val resp = JSONObject(String(error.networkResponse.data))
                Log.e("Resp", resp.toString())

                resp.getString("code")
            } catch (e: Exception) {
                UserParamErrors.UNKNOWN.name
            }

            val errorMsg = when (code) {
                UserParamErrors.ELECTION_NOT_ENDED.name -> "Election hasn't ended."
                else -> "Server error."
            }

            _snackBarError.value = Event(errorMsg)
        }
    }

    companion object {
        private const val USER_PARAM_URL = "https://snapfile.tech/voter/getVoterParamsAndOptions"
        private const val USER_ALPHA_URL = "https://snapfile.tech/voter/getAlphaAndTN"

        enum class UserParamErrors {
            ELECTION_NOT_STARTED,
            ELECTION_NOT_ENDED,
            UNKNOWN
        }
    }
}
