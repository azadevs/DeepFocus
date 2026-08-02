package com.azadevs.deepfocus.domain.usecase

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

/**
 * Created by : Azamat Kalmurzaev
 * 21/02/2026
 */
data class DeepFocusUseCases(
    val upsertSession: UpsertSessionUseCase,
    val getAllSessions: GetAllSessionUseCase,
    val getTotalFocusMinutes: GetTotalFocusMinutesUseCase,
    val getSessionsBetween: GetSessionsBetweenUseCase,
    val getFocusDuration: GetFocusDurationUseCase,
    val setFocusDuration: SetFocusDurationUseCase,
    val getShortBreakDuration: GetShortBreakDurationUseCase,
    val setShortBreakDuration: SetShortBreakDurationUseCase,
    val getLongBreakDuration: GetLongBreakDurationUseCase,
    val setLongBreakDuration: SetLongBreakDurationUseCase,
    val getStardust: GetStardustUseCase,
    val getCurrentStreak: GetCurrentStreakUseCase,
    val getBestStreak: GetBestStreakUseCase,
    val getOnboardingCompleted: GetOnboardingCompletedUseCase,
    val setOnboardingCompleted: SetOnboardingCompletedUseCase,
    val getTasks: GetTasksUseCase,
    val upsertTask: UpsertTaskUseCase,
    val deleteTask: DeleteTaskUseCase
)
