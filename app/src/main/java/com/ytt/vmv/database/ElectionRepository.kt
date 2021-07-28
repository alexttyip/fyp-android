package com.ytt.vmv.database

import androidx.annotation.WorkerThread
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ElectionRepository @Inject constructor(
    private val electionDao: ElectionDao,
) {

    val allElections: Flow<List<Election>> = electionDao.getAll()

    fun getByName(name: String) = electionDao.getElectionAndOptionsByName(name)

    fun getOnlyElectionByName(name: String) = electionDao.getElectionByName(name)

    @WorkerThread
    suspend fun insert(election: Election) {
        electionDao.insert(election)
    }

    @WorkerThread
    suspend fun insert(electionOption: ElectionOption) {
        electionDao.insert(electionOption)
    }

    @WorkerThread
    suspend fun insertAll(electionOptions: List<ElectionOption>) {
        electionDao.insertAll(electionOptions)
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