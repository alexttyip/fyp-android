package com.ytt.vmv.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.math.BigInteger

@Parcelize
@Entity
data class Election(
    @PrimaryKey val name: String,
    val voterId: Int,
    val numTellers: Int,
    val thresholdTellers: Int,
    val g: BigInteger, // generator
    val p: BigInteger, // prime
    val q: BigInteger, // prime factor of p-1
    var publicKeySignature: BigInteger? = null,
    var publicKeyTrapdoor: BigInteger? = null,
) : Parcelable {
    fun hasGeneratedKeyPairs() = (publicKeySignature != null) && (publicKeyTrapdoor != null)
}
