package com.ytt.vmv.models

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import com.ytt.vmv.CircleTransform

data class VoteOption(val name: String, val picUrl: String)

@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, picUrl: String) {
    Picasso.get()
        .load(picUrl)
        .resize(60, 60)
        .centerCrop()
        .transform(CircleTransform())
        .into(imageView)
}
