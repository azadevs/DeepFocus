package com.azadevs.deepfocus.di

import com.azadevs.deepfocus.data.repository.ProdFocusRepository
import com.azadevs.deepfocus.domain.repository.FocusRepository
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

}