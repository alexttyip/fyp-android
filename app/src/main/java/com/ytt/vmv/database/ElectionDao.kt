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

    @Query("SELECT * FROM ElectionTrackerNumber WHERE electionName  = :name")
    suspend fun getTNs(name: String): List<ElectionTrackerNumber>

    @Insert
    suspend fun insert(election: Election)

    @Insert
    suspend fun insert(electionOption: ElectionOption)

    @Insert
    suspend fun insertAllOptions(electionOptions: List<ElectionOption>)

    @Insert
    suspend fun insertAllTNs(trackerNumber: List<ElectionTrackerNumber>)

    @Update
    suspend fun update(election: Election)

    @Delete
    suspend fun delete(election: Election)

    @Transaction
    suspend fun deleteAll() {
        deleteAllElections()
        deleteAllOptions()
        deleteAllTNs()
    }

    @Query("DELETE FROM Election")
    suspend fun deleteAllElections()

    @Query("DELETE FROM ElectionOption")
    suspend fun deleteAllOptions()

    @Query("DELETE FROM ElectionTrackerNumber")
    suspend fun deleteAllTNs()

    @Query("SELECT COUNT(name) FROM election")
    suspend fun count(): Int
}