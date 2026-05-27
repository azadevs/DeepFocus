package com.azadevs.deepfocus.domain.usecase.cosmic

import com.azadevs.deepfocus.core.model.Resource
import com.azadevs.deepfocus.domain.repository.CosmicModuleRepository
import com.azadevs.deepfocus.domain.repository.FocusStreakRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * Created by : Azamat Kalmurzaev
 * 27/05/2026
 **/
class UnlockCosmicModuleUseCase @Inject constructor(
    private val streakRepository: FocusStreakRepository,
    private val cosmicRepository: CosmicModuleRepository
) {
    suspend operator fun invoke(moduleId: Int, cost: Int): Resource<Unit> {
        val unlockedIds = cosmicRepository.getUnlockedModuleIds().first()
        if (moduleId in unlockedIds) {
            return Resource.Error("Module already unlocked")
        }

        val currentStardust = streakRepository.getStardust().first()
        if (currentStardust < cost) {
            return Resource.Error("Not enough Stardust. Need $cost, have $currentStardust")
        }

        streakRepository.spendStardust(cost)
        cosmicRepository.unlockModule(moduleId)

        return Resource.Success(Unit)
    }
}
