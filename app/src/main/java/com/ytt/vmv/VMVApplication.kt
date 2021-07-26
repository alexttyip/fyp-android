package com.ytt.vmv

import android.app.Application
import com.ytt.vmv.network.NetworkSingleton
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VMVApplication : Application() {
    val network by lazy { NetworkSingleton.getInstance(this) }
}