package com.ytt.vmv.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ytt.vmv.database.entities.KeyPairs
import kotlinx.coroutines.flow.Flow

@Dao
interface KeyPairsDao {
    @Query("SELECT * FROM key_pairs")
    fun getAll(): Flow<List<KeyPairs>>

    @Query("SELECT * FROM key_pairs WHERE name = :name")
    suspend fun loadAllByIds(name: String): List<KeyPairs>

    @Insert
    suspend fun insert(key_pairss: KeyPairs)

    @Delete
    suspend fun delete(key_pairs: KeyPairs)

    @Query("DELETE FROM key_pairs")
    suspend fun deleteAll()
}