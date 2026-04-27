package com.ilmezubaan.app.di

import com.ilmezubaan.app.data.local.dao.UserStatsDao
import com.ilmezubaan.app.data.local.dao.ConceptDao
import com.ilmezubaan.app.data.local.dao.LanguageMetadataDao
import com.ilmezubaan.app.data.repository.UserStatsRepository
import com.ilmezubaan.app.data.repository.ConceptRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import com.ilmezubaan.app.data.repository.GeminiWordRepository
import com.ilmezubaan.app.data.remote.gemini.GeminiApiClient
import com.ilmezubaan.app.data.remote.gemini.GeminiApiService
import com.google.gson.Gson

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideGeminiApiService(): GeminiApiService = GeminiApiClient.createService()

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideGeminiWordRepository(
        apiService: GeminiApiService,
        gson: Gson
    ): GeminiWordRepository {
        return GeminiWordRepository(apiService, gson)
    }

    @Provides
    @Singleton
    fun provideUserStatsRepository(
        userStatsDao: UserStatsDao,
        conceptDao: ConceptDao,
        languageMetadataDao: LanguageMetadataDao
    ): UserStatsRepository {
        return UserStatsRepository(userStatsDao, conceptDao, languageMetadataDao)
    }

    @Provides
    @Singleton
    fun provideConceptRepository(
        conceptDao: ConceptDao,
        metadataDao: LanguageMetadataDao,
        firebaseDatabase: com.google.firebase.database.FirebaseDatabase
    ): ConceptRepository {
        return ConceptRepository(conceptDao, metadataDao, firebaseDatabase)
    }
}
