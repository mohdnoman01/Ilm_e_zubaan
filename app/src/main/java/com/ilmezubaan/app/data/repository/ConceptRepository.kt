package com.ilmezubaan.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.ilmezubaan.app.data.local.dao.ConceptDao
import com.ilmezubaan.app.data.local.dao.LanguageMetadataDao
import com.ilmezubaan.app.data.local.entities.ConceptEntity
import com.ilmezubaan.app.data.local.entities.LanguageMetadataEntity
import com.ilmezubaan.app.data.model.Concept
import com.ilmezubaan.app.data.model.LanguageDetail
import com.ilmezubaan.app.data.model.LanguageMetadata
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ConceptRepository(
    private val conceptDao: ConceptDao,
    private val metadataDao: LanguageMetadataDao,
    private val firestore: FirebaseFirestore
) {
    private val gson = Gson()

    val allConcepts: Flow<List<Concept>> = conceptDao.getAllConcepts().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getMetadata(langId: String): Flow<LanguageMetadata?> {
        return metadataDao.getMetadata(langId).map { it?.toDomain() }
    }

    suspend fun syncConcepts() {
        try {
            val snapshot = firestore.collection("concepts").get().await()
            val remoteConcepts = snapshot.documents.mapNotNull { doc ->
                val id = doc.id
                val english = doc.getString("englishMeaning") ?: ""
                val category = doc.getString("category") ?: ""
                val level = doc.getString("difficultyLevel") ?: ""
                val languages = doc.get("languages") as? Map<String, Any>
                val updatedAt = doc.getTimestamp("updatedAt")?.toDate()?.time ?: 0L

                if (languages != null) {
                    ConceptEntity(
                        conceptId = id,
                        englishMeaning = english,
                        category = category,
                        difficultyLevel = level,
                        languagesJson = gson.toJson(languages),
                        updatedAt = updatedAt
                    )
                } else null
            }
            conceptDao.insertConcepts(remoteConcepts)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun syncMetadata() {
        try {
            val snapshot = firestore.collection("language_metadata").get().await()
            snapshot.documents.forEach { doc ->
                val entity = LanguageMetadataEntity(
                    languageId = doc.id,
                    introVideoUrl = doc.getString("introVideoUrl") ?: "",
                    historyText = doc.getString("historyText") ?: "",
                    region = doc.getString("region") ?: "",
                    lifestyleBrief = doc.getString("lifestyleBrief") ?: "",
                    updatedAt = System.currentTimeMillis()
                )
                metadataDao.insertMetadata(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun insertConcepts(concepts: List<ConceptEntity>) {
        conceptDao.insertConcepts(concepts)
    }

    private fun ConceptEntity.toDomain(): Concept {
        val type = object : TypeToken<Map<String, LanguageDetail>>() {}.type
        val langMap: Map<String, LanguageDetail> = gson.fromJson(this.languagesJson, type)
        return Concept(
            conceptId = this.conceptId,
            englishMeaning = this.englishMeaning,
            category = this.category,
            difficultyLevel = this.difficultyLevel,
            languages = langMap,
            updatedAt = this.updatedAt
        )
    }

    private fun LanguageMetadataEntity.toDomain() = LanguageMetadata(
        languageId = languageId,
        introVideoUrl = introVideoUrl,
        historyText = historyText,
        region = region,
        lifestyleBrief = lifestyleBrief
    )
}
