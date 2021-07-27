package com.ytt.vmv.models

import android.app.Application
import androidx.lifecycle.*
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.ytt.vmv.Event
import com.ytt.vmv.cryptography.VoterKeyGenerator
import com.ytt.vmv.database.ElectionRepository
import com.ytt.vmv.network.NetworkSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class GenerateKeyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ElectionRepository,
    private val network: NetworkSingleton,
    application: Application,
) : AndroidViewModel(application), Response.ErrorListener, Response.Listener<String> {
    private val electionName: String =
        savedStateHandle.get<String>("election") ?: ""

    val election = repository.getOnlyElectionByName(electionName).asLiveData()

    val isOverlayVisible = MutableLiveData(false)
    private val _uploadResp = MutableLiveData<Event<String>>()
    private val _uploadError = MutableLiveData<Event<VolleyError>>()

    val uploadResp: LiveData<Event<String>>
        get() = _uploadResp
    val uploadError: LiveData<Event<VolleyError>>
        get() = _uploadError

    fun onGenKeys() {
        election.value?.apply {
            isOverlayVisible.value = true

            val applicationContext = getApplication<Application>().applicationContext
            val (signingPublic, trapdoorPublic) = VoterKeyGenerator.genAndStore(
                applicationContext, name, g, p, q
            )

            publicKeySignature = signingPublic
            publicKeyTrapdoor = trapdoorPublic

            // Save public keys
            viewModelScope.launch {
                repository.update(this@apply)
            }

            // Upload public keys
            val req =
                object : StringRequest(
                    Method.POST,
                    UPLOAD_KEYS_URL,
                    this@GenerateKeyViewModel,
                    this@GenerateKeyViewModel
                ) {
                    override fun getBody(): ByteArray {
                        val jsonObj = JSONObject()
                            .put("electionName", name)
                            .put("deviceId", deviceId)
                            .put("publicKeySignature", signingPublic.toString())
                            .put("publicKeyTrapdoor", trapdoorPublic.toString())

                        return jsonObj.toString().toByteArray()
                    }

                    override fun getBodyContentType() = "application/json; charset=utf-8"
                }

            network.addToRequestQueue(req)
        }
    }

    override fun onResponse(response: String) {
        isOverlayVisible.value = false
        _uploadResp.value = Event(response)
    }

    override fun onErrorResponse(error: VolleyError) {
        isOverlayVisible.value = false
        _uploadError.value = Event(error)
    }

    companion object {
        private const val UPLOAD_KEYS_URL = "https://snapfile.tech/voter/uploadKeys"
    }
}