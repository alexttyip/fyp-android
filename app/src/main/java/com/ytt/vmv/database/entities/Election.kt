package com.ytt.vmv.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.math.BigInteger

@Entity
@Parcelize
data class Election(
    @PrimaryKey val name: String,
    val numTellers: Int,
    val thresholdTellers: Int,
    val g: BigInteger, // generator
    val p: BigInteger, // prime
    val q: BigInteger, // prime factor of p-1
    var trapdoorPublicKey: BigInteger? = null,
    var trapdoorPrivateKey: BigInteger? = null,
    var signingPublicKey: BigInteger? = null,
    var signingPrivateKey: BigInteger? = null,
) : Parcelable {
    fun hasGeneratedKeyPairs(): Boolean =
        (trapdoorPublicKey != null)
                && (trapdoorPrivateKey != null)
                && (signingPublicKey != null)
                && (signingPrivateKey != null)
}