package com.ytt.vmv.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.ytt.vmv.database.Election
import com.ytt.vmv.database.ElectionRepository
import com.ytt.vmv.network.NetworkSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.math.BigInteger
import javax.inject.Inject

const val PARAMS_URL = "https://snapfile.tech/voter/getElectionParams"

@HiltViewModel
class ElectionViewModel @Inject constructor(
    private val repository: ElectionRepository,
    private val network: NetworkSingleton,
) : ViewModel() {
    val allElections = repository.allElections.asLiveData()

    fun updateFromRemote() = viewModelScope.launch {
        repository.updateFromRemote()
    }

    fun addElection(
        input: String,
        callback: () -> Unit,
        errorCallback: (error: String) -> Unit,
    ) {
        if (input.isBlank()) {
            errorCallback("Input cannot be blank.")
            return
        }

        Log.e("Input", input)

        val req = JsonObjectRequest(
            Request.Method.GET,
            "$PARAMS_URL/${input}",
            null,
            { response ->
                Log.e("Response", response.toString())

                saveElectionParams(input, response)

                callback()
            },
            { error ->
                Log.e("Error", error.toString())
                errorCallback(error.toString())
            }
        )

        network.addToRequestQueue(req)
    }

    private fun saveElectionParams(name: String, params: JSONObject) {
        // TODO numTellers, thresholdTellers
        val election = Election(
            name,
            4,
            3,
            BigInteger(params.getString("g")),
            BigInteger(params.getString("p")),
            BigInteger(params.getString("q")),
            BigInteger(params.getString("electionPublicKey")),
        )

        viewModelScope.launch {
            repository.insert(election)
        }
    }
}
