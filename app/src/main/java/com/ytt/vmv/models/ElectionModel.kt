package com.ytt.vmv.models

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

@Parcelize
data class ElectionModel(val name: String, private val date: Number) : ViewModel(), Parcelable {
    fun getDate(): String = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date)
}