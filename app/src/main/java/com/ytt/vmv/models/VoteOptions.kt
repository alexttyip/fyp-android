package com.ytt.vmv.models

import android.app.Application
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.squareup.picasso.Picasso
import com.ytt.vmv.CircleTransform
import com.ytt.vmv.network.NetworkSingleton
import org.json.JSONArray

const val URL = "http://10.0.2.2:3000/vote-options"

data class VoteOption(val name: String, val picUrl: String)

class VoteOptions(application: Application) : AndroidViewModel(application) {
    private val options: MutableLiveData<List<VoteOption>> by lazy {
        loadVoteOptions()
    }

    fun getVoteOptions(): LiveData<List<VoteOption>> {
        return options
    }

    private val handleResponse: Response.Listener<JSONArray> = Response.Listener { array ->
        options.value = (0 until array.length()).map {
            VoteOption(
                array.getJSONObject(it).getString("option"),
                "https://picsum.photos/id/${it * 10}/200"
            )
        }
    }

    private fun loadVoteOptions(): MutableLiveData<List<VoteOption>> {
        val jsonRequest = JsonArrayRequest(
            Request.Method.GET, URL, null,
            handleResponse,
            { Log.e("Response", it.toString()) })

        NetworkSingleton.getInstance(getApplication()).addToRequestQueue(jsonRequest)

        return MutableLiveData(listOf())
    }
}

@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, picUrl: String) {
    Picasso.get()
        .load(picUrl)
        .resize(60, 60)
        .centerCrop()
        .transform(CircleTransform())
        .into(imageView)
}