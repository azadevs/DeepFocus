package com.azadevs.deepfocus.di

import android.content.Context
import androidx.room.Room
import com.azadevs.deepfocus.data.local.FocusDatabase
import com.azadevs.deepfocus.data.local.dao.FocusSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

/**
 * Created by : Azamat Kalmurzaev
 * 20/02/2026
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): FocusDatabase {
        return Room.databaseBuilder(
            context,
            FocusDatabase::class.java,
            "deepfocus_db"
        ).build()
    }

    @Provides
    fun provideFocusSessionDao(
        database: FocusDatabase
    ): FocusSessionDao {
        return database.focusSessionDao()
    }
}