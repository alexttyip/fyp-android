package com.ytt.vmv.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ytt.vmv.database.entities.Election
import kotlinx.coroutines.flow.Flow

@Dao
interface ElectionDao {
    @Query("SELECT * FROM election")
    fun getAll(): Flow<List<Election>>

    @Query("SELECT * FROM election WHERE name = :name")
    suspend fun loadAllByIds(name: String): List<Election>

    @Insert
    suspend fun insert(elections: Election)

    @Delete
    suspend fun delete(election: Election)

    @Query("DELETE FROM election")
    suspend fun deleteAll()
}