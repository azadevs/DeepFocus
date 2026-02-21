package com.azadevs.deepfocus.domain.usecase.session

import com.azadevs.deepfocus.domain.repository.FocusRepository

/**
 * Created by : Azamat Kalmurzaev
 * 21/02/2026
 */
class GetSessionsBetweenUseCase(
    private val repository: FocusRepository
) {
    operator fun invoke(
        start: Long,
        end: Long
    ) = repository.getSessionsBetween(start, end)
}