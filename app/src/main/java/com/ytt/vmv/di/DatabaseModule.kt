package com.ytt.vmv.di

import android.content.Context
import com.ytt.vmv.database.AppDatabase
import com.ytt.vmv.database.ElectionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideElectionDao(appDatabase: AppDatabase): ElectionDao {
        return appDatabase.electionDao()
    }
}