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
    var beta: BigInteger? = null,
    var encryptedTrackerNumberInGroup: String? = null,
    var hasVoted: Boolean = false,
    var alpha: BigInteger? = null,
    val deviceId: String = UUID.randomUUID().toString(),
) : Parcelable {
    fun hasGeneratedKeyPairs() =
        hasVoted || (publicKeySignature != null && publicKeyTrapdoor != null)

    fun hasObtainedUserParams() =
        hasVoted || (hasGeneratedKeyPairs() && beta != null && encryptedTrackerNumberInGroup != null)
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

@Entity
data class ElectionTrackerNumber(
    val trackerNumber: BigInteger,
    val trackerNumberInGroup: BigInteger,
    val encryptedTrackerNumberInGroup: String,
    val electionName: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)

data class ElectionAndTrackerNumbers(
    @Embedded val election: Election,
    @Relation(
        parentColumn = "name",
        entityColumn = "electionName"
    )
    val tns: List<ElectionTrackerNumber>,
)