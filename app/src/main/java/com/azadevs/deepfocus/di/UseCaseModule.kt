package com.azadevs.deepfocus.di

import com.azadevs.deepfocus.domain.usecase.DeepFocusUseCases
import com.azadevs.deepfocus.domain.usecase.gamification.GetBestStreakUseCase
import com.azadevs.deepfocus.domain.usecase.gamification.GetCurrentStreakUseCase
import com.azadevs.deepfocus.domain.usecase.gamification.GetStardustUseCase
import com.azadevs.deepfocus.domain.usecase.session.GetAllSessionUseCase
import com.azadevs.deepfocus.domain.usecase.session.GetSessionsBetweenUseCase
import com.azadevs.deepfocus.domain.usecase.session.GetTotalFocusMinutesUseCase
import com.azadevs.deepfocus.domain.usecase.session.UpsertSessionUseCase
import com.azadevs.deepfocus.domain.usecase.settings.GetFocusDurationUseCase
import com.azadevs.deepfocus.domain.usecase.settings.GetLongBreakDurationUseCase
import com.azadevs.deepfocus.domain.usecase.settings.GetOnboardingCompletedUseCase
import com.azadevs.deepfocus.domain.usecase.settings.GetShortBreakDurationUseCase
import com.azadevs.deepfocus.domain.usecase.settings.SetFocusDurationUseCase
import com.azadevs.deepfocus.domain.usecase.settings.SetLongBreakDurationUseCase
import com.azadevs.deepfocus.domain.usecase.settings.SetOnboardingCompletedUseCase
import com.azadevs.deepfocus.domain.usecase.settings.SetShortBreakDurationUseCase
import com.azadevs.deepfocus.domain.usecase.task.DeleteTaskUseCase
import com.azadevs.deepfocus.domain.usecase.task.GetTasksUseCase
import com.azadevs.deepfocus.domain.usecase.task.UpsertTaskUseCase
import com.azadevs.deepfocus.domain.usecase.settings.GetSoundEnabledUseCase
import com.azadevs.deepfocus.domain.usecase.settings.SetSoundEnabledUseCase
import com.azadevs.deepfocus.domain.usecase.settings.GetVibrationEnabledUseCase
import com.azadevs.deepfocus.domain.usecase.settings.SetVibrationEnabledUseCase
import com.azadevs.deepfocus.domain.usecase.settings.GetAutoStartBreaksUseCase
import com.azadevs.deepfocus.domain.usecase.settings.SetAutoStartBreaksUseCase
import com.azadevs.deepfocus.domain.usecase.settings.GetThemeModeUseCase
import com.azadevs.deepfocus.domain.usecase.settings.SetThemeModeUseCase
import com.azadevs.deepfocus.domain.usecase.settings.GetAmbientSoundUseCase
import com.azadevs.deepfocus.domain.usecase.settings.SetAmbientSoundUseCase
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
        setDuration: SetFocusDurationUseCase,
        getShortBreakDuration: GetShortBreakDurationUseCase,
        setShortBreakDuration: SetShortBreakDurationUseCase,
        getLongBreakDuration: GetLongBreakDurationUseCase,
        setLongBreakDuration: SetLongBreakDurationUseCase,
        getStardust: GetStardustUseCase,
        getCurrentStreak: GetCurrentStreakUseCase,
        getBestStreak: GetBestStreakUseCase,
        getOnboardingCompleted: GetOnboardingCompletedUseCase,
        setOnboardingCompleted: SetOnboardingCompletedUseCase,
        getTasks: GetTasksUseCase,
        upsertTask: UpsertTaskUseCase,
        deleteTask: DeleteTaskUseCase,
        getSoundEnabled: GetSoundEnabledUseCase,
        setSoundEnabled: SetSoundEnabledUseCase,
        getVibrationEnabled: GetVibrationEnabledUseCase,
        setVibrationEnabled: SetVibrationEnabledUseCase,
        getAutoStartBreaks: GetAutoStartBreaksUseCase,
        setAutoStartBreaks: SetAutoStartBreaksUseCase,
        getThemeMode: GetThemeModeUseCase,
        setThemeMode: SetThemeModeUseCase,
        getAmbientSound: GetAmbientSoundUseCase,
        setAmbientSound: SetAmbientSoundUseCase
    ): DeepFocusUseCases {
        return DeepFocusUseCases(
            upsert,
            getAll,
            total,
            between,
            getDuration,
            setDuration,
            getShortBreakDuration,
            setShortBreakDuration,
            getLongBreakDuration,
            setLongBreakDuration,
            getStardust,
            getCurrentStreak,
            getBestStreak,
            getOnboardingCompleted,
            setOnboardingCompleted,
            getTasks,
            upsertTask,
            deleteTask,
            getSoundEnabled,
            setSoundEnabled,
            getVibrationEnabled,
            setVibrationEnabled,
            getAutoStartBreaks,
            setAutoStartBreaks,
            getThemeMode,
            setThemeMode,
            getAmbientSound,
            setAmbientSound
        )
    }
}