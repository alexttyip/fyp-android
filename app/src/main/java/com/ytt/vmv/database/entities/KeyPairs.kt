package com.ytt.vmv.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.math.BigInteger

@Entity(tableName = "key_pairs")
@Parcelize
data class KeyPairs(
    @PrimaryKey val name: String,
    var trapdoorPublicKey: BigInteger? = null,
    var trapdoorPrivateKey: BigInteger? = null,
    var signingPublicKey: BigInteger? = null,
    var signingPrivateKey: BigInteger? = null,
) : Parcelable