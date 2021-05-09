package com.ytt.vmv.models

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.squareup.picasso.Picasso
import com.ytt.vmv.CircleTransform

data class VoteOption(val name: String, val picUrl: String)

class VoteOptions : ViewModel() {
    private val options: MutableLiveData<List<VoteOption>> by lazy {
        loadVoteOptions()
    }

    fun getVoteOptions(): LiveData<List<VoteOption>> {
        return options
    }

    private fun loadVoteOptions(): MutableLiveData<List<VoteOption>> {
        return MutableLiveData(List(4) {
            VoteOption(
                "$it",
                "https://picsum.photos/id/${it * 10}/200"
            )
        })
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
