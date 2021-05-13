package com.ytt.vmv

import android.app.Application
import com.ytt.vmv.database.AppDatabase
import com.ytt.vmv.database.repos.ElectionRepository
import com.ytt.vmv.network.NetworkSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class VMVApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }

    val electionRepository by lazy { ElectionRepository(database.electionDao()) }

    val network by lazy { NetworkSingleton.getInstance(this) }
}