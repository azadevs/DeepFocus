package com.azadevs.deepfocus.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Created by : Azamat Kalmurzaev
 * 27/05/2026
 */
interface CosmicModuleRepository {
    fun getUnlockedModuleIds(): Flow<Set<Int>>
    suspend fun unlockModule(moduleId: Int)
}
