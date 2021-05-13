package com.ytt.vmv.database

import androidx.annotation.WorkerThread
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
}