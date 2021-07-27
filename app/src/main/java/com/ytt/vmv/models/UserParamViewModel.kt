package com.ytt.vmv.models

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class UserParamViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val beta = BigInteger(savedStateHandle.get<String>("beta") ?: "0")
    val encryptedTNIG = BigInteger(savedStateHandle.get<String>("encryptedTNIG") ?: "0")
}