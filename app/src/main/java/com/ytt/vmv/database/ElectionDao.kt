package com.ytt.vmv.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ElectionDao {
    @Query("SELECT * FROM election")
    fun getAll(): Flow<List<Election>>

    @Query("SELECT * FROM election WHERE name = :name")
    suspend fun loadAllByIds(name: String): List<Election>

    @Insert
    suspend fun insert(election: Election)

    @Update
    suspend fun update(election: Election)

    @Delete
    suspend fun delete(election: Election)

    @Query("DELETE FROM election")
    suspend fun deleteAll()
}