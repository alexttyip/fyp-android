package com.ytt.vmv.models

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import com.ytt.vmv.cryptography.KeyPair
import com.ytt.vmv.cryptography.Parameters
import kotlinx.parcelize.Parcelize
import java.math.BigInteger

@Parcelize
class KeyModel private constructor(
    val election: ElectionModel,
    val parameter: Parameters,
    val trapdoor: KeyPair,
    val signing: KeyPair
) : ViewModel(), Parcelable {
    companion object {
        fun generateKeys(election: ElectionModel, parameter: Parameters): KeyModel {
            // TODO generate keys
            val trapdoor = KeyPair(BigInteger("123"), BigInteger("456"))
            val signing = KeyPair(BigInteger("123"), BigInteger("456"))

            return KeyModel(
                election,
                parameter,
                trapdoor,
                signing
            )
        }

        fun withKeys(
            election: ElectionModel,
            parameter: Parameters,
            trapdoor: KeyPair,
            signing: KeyPair
        ) = KeyModel(
            election,
            parameter,
            trapdoor,
            signing
        )
    }
}