package com.azadevs.deepfocus.di

import android.content.Context
import com.azadevs.deepfocus.data.datastore.GamificationDataStore
import com.azadevs.deepfocus.data.datastore.SettingsDataStore
import com.azadevs.deepfocus.data.datastore.TimerPersistenceDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

/**
 * Created by : Azamat Kalmurzaev
 * 20/02/2026
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): SettingsDataStore {
        return SettingsDataStore(context)
    }

    @Provides
    @Singleton
    fun provideTimerDataStore(
        @ApplicationContext context: Context
    ): TimerPersistenceDataStore {
        return TimerPersistenceDataStore(context)
    }

    @Provides
    @Singleton
    fun provideGamificationDataStore(
        @ApplicationContext context: Context
    ): GamificationDataStore {
        return GamificationDataStore(context)
    }
}