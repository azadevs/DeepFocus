package com.azadevs.deepfocus.domain.model

import androidx.annotation.StringRes

/**
 * Created by : Azamat Kalmurzaev
 * 27/05/2026
 */
data class CosmicModule(
    val id: Int,
    val emoji: String,
    @param:StringRes val nameResId: Int,
    @param:StringRes val descriptionResId: Int,
    val stardustCost: Int,
    val isUnlocked: Boolean = false
)
