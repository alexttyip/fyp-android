package com.ytt.vmv.models

import androidx.lifecycle.ViewModel
import java.text.DateFormat
import java.util.*

data class ElectionModel(val name: String, private val date: Date) : ViewModel() {
    fun getDate(): String = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date)
}