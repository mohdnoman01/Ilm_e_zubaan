package com.ilmezubaan.app.di

import android.content.Context
import androidx.room.Room
import com.ilmezubaan.app.data.local.AppDatabase
import com.ilmezubaan.app.data.local.dao.UserStatsDao
import com.ilmezubaan.app.data.local.dao.ConceptDao
import com.ilmezubaan.app.data.local.dao.LanguageMetadataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideUserStatsDao(database: AppDatabase): UserStatsDao = database.userStatsDao()

    @Provides
    fun provideConceptDao(database: AppDatabase): ConceptDao = database.conceptDao()

    @Provides
    fun provideLanguageMetadataDao(database: AppDatabase): LanguageMetadataDao = database.languageMetadataDao()
}
