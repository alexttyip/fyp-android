package com.ytt.vmv.database

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.parcelize.Parcelize
import java.math.BigInteger
import java.util.*

@Parcelize
@Entity
data class Election(
    @PrimaryKey val name: String,
    val numTellers: Int,
    val thresholdTellers: Int,
    val g: BigInteger, // generator
    val p: BigInteger, // prime
    val q: BigInteger, // prime factor of p-1
    val electionPublicKey: BigInteger,
    var publicKeySignature: BigInteger? = null,
    var publicKeyTrapdoor: BigInteger? = null,
    val deviceId: String = UUID.randomUUID().toString(),
) : Parcelable {
    fun hasGeneratedKeyPairs() = (publicKeySignature != null) && (publicKeyTrapdoor != null)

    fun getViewKeysText() = if (hasGeneratedKeyPairs()) "View Keys" else "Generate Keys"
}

@Parcelize
@Entity
data class ElectionOption(
    val option: String,
    val optionNumberInGroup: BigInteger,
    val electionName: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
) : Parcelable

@Parcelize
data class ElectionAndOptions(
    @Embedded val election: Election,
    @Relation(
        parentColumn = "name",
        entityColumn = "electionName"
    )
    val options: List<ElectionOption>,
) : Parcelable {
    fun hasElectionStarted() = options.isNotEmpty()
}
