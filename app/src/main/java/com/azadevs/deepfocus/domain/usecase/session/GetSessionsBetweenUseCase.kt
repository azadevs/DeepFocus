package com.azadevs.deepfocus.domain.usecase.session

import com.azadevs.deepfocus.domain.model.FocusSession
import com.azadevs.deepfocus.domain.repository.FocusRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by : Azamat Kalmurzaev
 * 21/02/2026
 */
class GetSessionsBetweenUseCase @Inject constructor(
    private val repository: FocusRepository
) {
    operator fun invoke(
        start: Long,
        end: Long
    ): Flow<List<FocusSession>> {
        return repository.getSessionsBetween(start, end)
    }

}