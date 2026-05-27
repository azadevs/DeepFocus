package com.azadevs.deepfocus.data.repository

import com.azadevs.deepfocus.data.datastore.GamificationDataStore
import com.azadevs.deepfocus.domain.repository.CosmicModuleRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Created by : Azamat Kalmurzaev
 * 27/05/2026
 */
class ProdCosmicModuleRepository @Inject constructor(
    private val dataStore: GamificationDataStore
) : CosmicModuleRepository {

    override fun getUnlockedModuleIds(): Flow<Set<Int>> = dataStore.unlockedModuleIdsFlow

    override suspend fun unlockModule(moduleId: Int) {
        dataStore.unlockModule(moduleId)
    }
}
