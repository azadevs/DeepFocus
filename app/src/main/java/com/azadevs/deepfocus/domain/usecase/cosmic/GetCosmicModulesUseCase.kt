package com.azadevs.deepfocus.domain.usecase.cosmic

import com.azadevs.deepfocus.R
import com.azadevs.deepfocus.domain.model.CosmicModule
import com.azadevs.deepfocus.domain.repository.CosmicModuleRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Created by : Azamat Kalmurzaev
 * 27/05/2026
 */
class GetCosmicModulesUseCase @Inject constructor(
    private val repository: CosmicModuleRepository
) {
    private val catalog: List<CosmicModule> = listOf(
        CosmicModule(1, "🛸", R.string.module_1_name, R.string.module_1_desc, 50),
        CosmicModule(2, "🌐", R.string.module_2_name, R.string.module_2_desc, 150),
        CosmicModule(3, "🔭", R.string.module_3_name, R.string.module_3_desc, 300),
        CosmicModule(4, "🧪", R.string.module_4_name, R.string.module_4_desc, 500),
        CosmicModule(5, "☀️", R.string.module_5_name, R.string.module_5_desc, 750),
        CosmicModule(6, "🌱", R.string.module_6_name, R.string.module_6_desc, 1000),
        CosmicModule(7, "🛡️", R.string.module_7_name, R.string.module_7_desc, 1500),
        CosmicModule(8, "🚀", R.string.module_8_name, R.string.module_8_desc, 2000),
        CosmicModule(9, "🌌", R.string.module_9_name, R.string.module_9_desc, 3000),
        CosmicModule(10, "👑", R.string.module_10_name, R.string.module_10_desc, 5000),
    )

    operator fun invoke(): Flow<List<CosmicModule>> {
        return repository.getUnlockedModuleIds().map { unlockedIds ->
            catalog.map { module -> module.copy(isUnlocked = module.id in unlockedIds) }
        }
    }
}
