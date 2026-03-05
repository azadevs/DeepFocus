package com.azadevs.deepfocus.domain.usecase.gamification

import com.azadevs.deepfocus.domain.repository.FocusStreakRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStardustUseCase @Inject constructor(
    private val repository: FocusStreakRepository
) {
    operator fun invoke(): Flow<Int> = repository.getStardust()
}
