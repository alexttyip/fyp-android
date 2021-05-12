package com.ytt.vmv.cryptography

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigInteger

@Parcelize
data class KeyPair(
    val privateKey: BigInteger,
    val publicKey: BigInteger,
) : Parcelable

@Parcelize
data class Parameters(
//        var name: String,
//        var numTellers: Int,
//        var thresholdTellers: Int,
    val g: BigInteger, // generator
    val p: BigInteger, // prime
    val q: BigInteger // prime factor of p-1
) : Parcelable
