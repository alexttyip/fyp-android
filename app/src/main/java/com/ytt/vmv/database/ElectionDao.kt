package com.ytt.vmv.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface ElectionDao {
    @Query("SELECT * FROM election")
    fun getAll(): Flow<List<Election>>

    @Transaction
    @Query("SELECT * FROM election WHERE name = :name LIMIT 1")
    fun getElectionAndOptionsByName(name: String): Flow<ElectionAndOptions>

    @Insert
    suspend fun insert(election: Election)

    @Insert
    suspend fun insert(electionOption: ElectionOption)

    @Update
    suspend fun update(election: Election)

    @Delete
    suspend fun delete(election: Election)

    @Query("DELETE FROM election")
    suspend fun deleteAll()

    @Query("SELECT COUNT(name) FROM election")
    suspend fun count(): Int
}