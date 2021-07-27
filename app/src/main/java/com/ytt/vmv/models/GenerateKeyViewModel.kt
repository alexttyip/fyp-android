package com.ytt.vmv.models

import android.app.Application
import androidx.lifecycle.*
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.ytt.vmv.cryptography.VoterKeyGenerator
import com.ytt.vmv.database.ElectionRepository
import com.ytt.vmv.fragments.UPLOAD_KEYS_URL
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
    val uploadResp = MutableLiveData<String?>(null)
    val uploadError = MutableLiveData<VolleyError?>(null)

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

    override fun onResponse(response: String?) {
        isOverlayVisible.value = false
        uploadResp.value = response
    }

    override fun onErrorResponse(error: VolleyError?) {
        isOverlayVisible.value = false
        uploadError.value = error
    }

}