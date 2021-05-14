package com.ytt.vmv.cryptography

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigInteger

@Parcelize
data class PublicKeys(
    val trapdoorPublicKey: BigInteger,
    val signingPublicKey: BigInteger,
) : Parcelable {
    companion object {
        fun fromKeys(keys: Keys) = PublicKeys(keys.trapdoorPublicKey, keys.signingPublicKey)
    }
}


@Parcelize
data class Keys(
    val trapdoorPrivateKey: BigInteger,
    val trapdoorPublicKey: BigInteger,
    val signingPrivateKey: BigInteger,
    val signingPublicKey: BigInteger,
) : Parcelable

@Parcelize
data class Parameters(
    val g: BigInteger, // generator
    val p: BigInteger, // prime
    val q: BigInteger // prime factor of p-1
) : Parcelable
