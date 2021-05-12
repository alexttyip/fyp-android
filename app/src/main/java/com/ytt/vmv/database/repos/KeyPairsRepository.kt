package com.ytt.vmv.database.repos

import androidx.annotation.WorkerThread
import com.ytt.vmv.database.dao.KeyPairsDao
import com.ytt.vmv.database.entities.KeyPairs
import kotlinx.coroutines.flow.Flow

class KeyPairsRepository(private val keyPairsDao: KeyPairsDao) {

    val allKeyPairs: Flow<List<KeyPairs>> = keyPairsDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(keyPairs: KeyPairs) {
        keyPairsDao.insert(keyPairs)
    }
}