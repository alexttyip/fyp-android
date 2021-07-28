package com.ytt.vmv.models

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.ytt.vmv.cryptography.VoterKeyGenerator
import com.ytt.vmv.database.Election
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ViewKeyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext applicationContext: Context,
) : ViewModel() {
    val election = savedStateHandle.get<Election>("election")

    val privateKeySignature by lazy {
        election?.let {
            VoterKeyGenerator.getPrivateKey(
                applicationContext, it.name,
                VoterKeyGenerator.PrivateKey.SIGNATURE_PRIVATE_KEY
            )
        }
    }

    val privateKeyTrapdoor by lazy {
        election?.let {
            VoterKeyGenerator.getPrivateKey(
                applicationContext, it.name,
                VoterKeyGenerator.PrivateKey.TRAPDOOR_PRIVATE_KEY
            )
        }
    }
}
