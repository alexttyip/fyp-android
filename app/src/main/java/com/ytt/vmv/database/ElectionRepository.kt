package com.ytt.vmv.database

import androidx.annotation.WorkerThread
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class ElectionRepository(private val electionDao: ElectionDao) {

    val allElections: Flow<List<Election>> = electionDao.getAll()

    @WorkerThread
    suspend fun insert(election: Election) {
        electionDao.insert(election)
    }

    @WorkerThread
    suspend fun update(election: Election) {
        electionDao.update(election)
    }

    @WorkerThread
    suspend fun updateFromRemote() {
        delay(5000L)
        println("Wait is over")
        // TODO("Not yet implemented")
    }
}