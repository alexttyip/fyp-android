package com.ytt.vmv.database

import androidx.room.TypeConverter
import java.math.BigInteger

class Converters {
    @TypeConverter
    fun fromBigInteger(bi: BigInteger?) = bi?.toString()

    @TypeConverter
    fun toBigInteger(s: String?) = s?.let { BigInteger(it) }
}
