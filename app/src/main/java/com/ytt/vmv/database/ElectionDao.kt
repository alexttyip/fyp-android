package com.ytt.vmv.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface ElectionDao {
    @Query("SELECT * FROM election")
    fun getAll(): Flow<List<Election>>

    @Transaction
    @Query("SELECT * FROM election WHERE name = :name")
    fun getElectionAndOptionsByName(name: String): Flow<ElectionAndOptions>

    @Query("SELECT * FROM election WHERE name = :name")
    fun getElectionByName(name: String): Flow<Election>

    @Insert
    suspend fun insert(election: Election)

    @Insert
    suspend fun insert(electionOption: ElectionOption)

    @Insert
    suspend fun insertAll(electionOptions: List<ElectionOption>)

    @Update
    suspend fun update(election: Election)

    @Delete
    suspend fun delete(election: Election)

    @Query("DELETE FROM election")
    suspend fun deleteAll()

    @Query("SELECT COUNT(name) FROM election")
    suspend fun count(): Int
}