package com.azadevs.deepfocus.domain.usecase

import com.azadevs.deepfocus.domain.usecase.session.GetAllSessionUseCase
import com.azadevs.deepfocus.domain.usecase.session.GetSessionsBetweenUseCase
import com.azadevs.deepfocus.domain.usecase.session.GetTotalFocusMinutesUseCase
import com.azadevs.deepfocus.domain.usecase.session.UpsertSessionUseCase
import com.azadevs.deepfocus.domain.usecase.settings.GetFocusDurationUseCase
import com.azadevs.deepfocus.domain.usecase.settings.GetLongBreakDurationUseCase
import com.azadevs.deepfocus.domain.usecase.settings.GetShortBreakDurationUseCase
import com.azadevs.deepfocus.domain.usecase.settings.SetFocusDurationUseCase
import com.azadevs.deepfocus.domain.usecase.settings.SetLongBreakDurationUseCase
import com.azadevs.deepfocus.domain.usecase.settings.SetShortBreakDurationUseCase

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
    val setLongBreakDuration: SetLongBreakDurationUseCase
)
