package com.ytt.vmv.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface ElectionDao {
    @Query("SELECT * FROM Election")
    fun getAll(): Flow<List<Election>>

    @Transaction
    @Query("SELECT * FROM Election WHERE name = :name")
    fun getElectionAndOptionsByName(name: String): Flow<ElectionAndOptions>

    @Query("SELECT * FROM Election WHERE name = :name")
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

    @Transaction
    suspend fun deleteAll() {
        deleteAllElections()
        deleteAllOptions()
    }

    @Query("DELETE FROM Election")
    suspend fun deleteAllElections()

    @Query("DELETE FROM ElectionOption")
    suspend fun deleteAllOptions()

    @Query("SELECT COUNT(name) FROM election")
    suspend fun count(): Int
}