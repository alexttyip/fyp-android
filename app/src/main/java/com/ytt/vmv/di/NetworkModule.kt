package com.ytt.vmv.di

import android.content.Context
import com.ytt.vmv.network.NetworkSingleton
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    @Singleton
    @Provides
    fun provideNetworkService(@ApplicationContext applicationContext: Context): NetworkSingleton {
        return NetworkSingleton.getInstance(applicationContext)
    }
}
