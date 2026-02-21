package com.azadevs.deepfocus.di

import com.azadevs.deepfocus.domain.usecase.DeepFocusUseCases
import com.azadevs.deepfocus.domain.usecase.session.GetAllSessionUseCase
import com.azadevs.deepfocus.domain.usecase.session.GetSessionsBetweenUseCase
import com.azadevs.deepfocus.domain.usecase.session.GetTotalFocusMinutesUseCase
import com.azadevs.deepfocus.domain.usecase.session.UpsertSessionUseCase
import com.azadevs.deepfocus.domain.usecase.settings.GetFocusDurationUseCase
import com.azadevs.deepfocus.domain.usecase.settings.SetFocusDurationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

/**
 * Created by : Azamat Kalmurzaev
 * 21/02/2026
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideDeepFocusUseCases(
        upsert: UpsertSessionUseCase,
        getAll: GetAllSessionUseCase,
        total: GetTotalFocusMinutesUseCase,
        between: GetSessionsBetweenUseCase,
        getDuration: GetFocusDurationUseCase,
        setDuration: SetFocusDurationUseCase
    ): DeepFocusUseCases {
        return DeepFocusUseCases(
            upsert,
            getAll,
            total,
            between,
            getDuration,
            setDuration
        )
    }

}