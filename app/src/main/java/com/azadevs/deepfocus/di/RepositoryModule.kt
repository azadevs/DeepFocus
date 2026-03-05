package com.azadevs.deepfocus.di

import com.azadevs.deepfocus.data.datastore.TimerPersistenceDataStore
import com.azadevs.deepfocus.data.repository.ProdFocusRepository
import com.azadevs.deepfocus.data.repository.ProdSettingsRepository
import com.azadevs.deepfocus.domain.repository.FocusRepository
import com.azadevs.deepfocus.domain.repository.SettingsRepository
import com.azadevs.deepfocus.domain.repository.TimerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

/**
 * Created by : Azamat Kalmurzaev
 * 20/02/2026
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFocusRepository(
        prodFocusRepository: ProdFocusRepository
    ): FocusRepository

    @Binds
    @Singleton
    abstract fun bindTimerRepository(
        impl: TimerPersistenceDataStore
    ): TimerRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: ProdSettingsRepository
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindFocusStreakRepository(
        impl: com.azadevs.deepfocus.data.repository.ProdFocusStreakRepository
    ): com.azadevs.deepfocus.domain.repository.FocusStreakRepository
}