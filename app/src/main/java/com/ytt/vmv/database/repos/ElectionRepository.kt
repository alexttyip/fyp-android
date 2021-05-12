package com.ytt.vmv.database.repos

import androidx.annotation.WorkerThread
import com.ytt.vmv.database.dao.ElectionDao
import com.ytt.vmv.database.entities.Election
import kotlinx.coroutines.flow.Flow

class ElectionRepository(private val electionDao: ElectionDao) {

    val allElections: Flow<List<Election>> = electionDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(election: Election) {
        electionDao.insert(election)
    }
}