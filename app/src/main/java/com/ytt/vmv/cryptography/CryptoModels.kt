package com.ytt.vmv.cryptography

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigInteger

@Parcelize
data class PublicKeys(
    val publicKeySignature: BigInteger,
    val publicKeyTrapdoor: BigInteger,
) : Parcelable
/* {
    companion object {
        fun fromKeys(keys: Keys) = PublicKeys(keys.publicKeySignature, keys.publicKeyTrapdoor)
    }
} */

@Parcelize
data class Keys(
    val privateKeySignature: BigInteger,
    val publicKeySignature: BigInteger,
    val privateKeyTrapdoor: BigInteger,
    val publicKeyTrapdoor: BigInteger,
) : Parcelable

@Parcelize
data class Parameters(
    val g: BigInteger, // generator
    val p: BigInteger, // prime
    val q: BigInteger // prime factor of p-1
) : Parcelable
