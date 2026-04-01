package com.ilmezubaan.app.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ilmezubaan.app.data.local.dao.ConceptDao
import com.ilmezubaan.app.data.local.dao.LanguageMetadataDao
import com.ilmezubaan.app.data.local.entities.ConceptEntity
import com.ilmezubaan.app.data.local.entities.LanguageMetadataEntity
import com.ilmezubaan.app.data.model.Concept
import com.ilmezubaan.app.data.model.LanguageDetail
import com.ilmezubaan.app.data.model.LanguageMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ConceptRepository(
    private val conceptDao: ConceptDao,
    private val metadataDao: LanguageMetadataDao,
    private val firebaseDatabase: FirebaseDatabase
) {
    private val gson = Gson()
    private val dbRef = firebaseDatabase.reference
    private val auth = FirebaseAuth.getInstance()

    val allConcepts: Flow<List<Concept>> = conceptDao.getAllConcepts().map { entities ->
        entities.map { it.toDomain() }
    }

    private suspend fun ensureAuthenticated(): Boolean {
        if (auth.currentUser == null) {
            return try {
                auth.signInAnonymously().await()
                Log.d("FirebaseSync", "Signed in anonymously: ${auth.currentUser?.uid}")
                true
            } catch (e: Exception) {
                Log.e("FirebaseSync", "Anonymous sign-in failed", e)
                false
            }
        }
        return true
    }

    fun getMetadata(langId: String): Flow<LanguageMetadata?> {
        return metadataDao.getMetadata(langId).map { it?.toDomain() }
    }

    suspend fun syncConcepts() = withContext(Dispatchers.IO) {
        try {
            Log.d("FirebaseSync", "Starting Sync... User: ${auth.currentUser?.uid}")
            if (!ensureAuthenticated()) {
                Log.e("FirebaseSync", "Sync failed: Not authenticated")
                return@withContext
            }
            
            val snapshot = dbRef.get().await()
            
            if (!snapshot.exists()) {
                Log.e("FirebaseSync", "Database is empty!")
                return@withContext
            }

            // Data structure handle karne ke liye check
            val languagesNode = snapshot.child("Languages")
            val targetSnapshot = if (languagesNode.exists()) languagesNode else snapshot

            val allRemoteConcepts = mutableListOf<ConceptEntity>()

            targetSnapshot.children.forEach { langFolder ->
                val langName = langFolder.key?.lowercase() ?: return@forEach
                if (langName == "language_metadata" || langName == "users" || langName == "languages") return@forEach

                langFolder.children.forEach { conceptSnapshot ->
                    val data = conceptSnapshot
                    val english = data.child("english_meaning").value?.toString()
                        ?: data.child("english").value?.toString()
                        ?: ""
                    
                    if (english.isNotEmpty()) {
                        val id = "${langName}_${data.key}"
                        val category = data.child("category").value?.toString() ?: "General"
                        val level = data.child("level").value?.toString() ?: "Basic"
                        val audioUrl = data.child("audio_url").value?.toString() ?: ""
                        
                        val example = data.child("${langName}_example").value?.toString()
                            ?: data.child("example").value?.toString()
                            ?: ""
                        
                        val exampleMeaning = data.child("${langName}_example_meaning").value?.toString()
                            ?: data.child("example_meaning").value?.toString()
                            ?: ""
                        
                        val languages = mutableMapOf<String, LanguageDetail>()
                        
                        val nativeScript = data.child("${langName}_shahmukhi").value?.toString()
                            ?: data.child("${langName}_script").value?.toString()
                            ?: data.child("urdu_meaning").value?.toString()
                            ?: data.child("native_script").value?.toString()
                            ?: data.child("script").value?.toString()
                            ?: ""

                        languages[langName] = LanguageDetail(
                            script = nativeScript,
                            roman = data.child("roman").value?.toString() ?: "",
                            audioUrl = audioUrl,
                            example = example,
                            exampleMeaning = exampleMeaning
                        )

                        allRemoteConcepts.add(
                            ConceptEntity(
                                conceptId = id,
                                englishMeaning = english,
                                category = category,
                                difficultyLevel = level,
                                languagesJson = gson.toJson(languages),
                                updatedAt = System.currentTimeMillis()
                            )
                        )
                    }
                }
            }
            
            if (allRemoteConcepts.isNotEmpty()) {
                conceptDao.deleteAll()
                conceptDao.insertConcepts(allRemoteConcepts)
                Log.d("FirebaseSync", "Successfully synced ${allRemoteConcepts.size} concepts.")
            }
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Sync failed: ${e.message}", e)
        }
    }

    suspend fun syncMetadata() = withContext(Dispatchers.IO) {
        try {
            if (!ensureAuthenticated()) return@withContext
            val snapshot = dbRef.child("language_metadata").get().await()
            snapshot.children.forEach { child ->
                val entity = LanguageMetadataEntity(
                    languageId = child.key ?: "",
                    introVideoUrl = child.child("introVideoUrl").value?.toString() ?: "",
                    historyText = child.child("historyText").value?.toString() ?: "",
                    region = child.child("region").value?.toString() ?: "",
                    lifestyleBrief = child.child("lifestyleBrief").value?.toString() ?: "",
                    updatedAt = System.currentTimeMillis()
                )
                metadataDao.insertMetadata(entity)
            }
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Metadata sync failed: ${e.message}")
        }
    }

    suspend fun insertConcepts(concepts: List<ConceptEntity>) = withContext(Dispatchers.IO) {
        conceptDao.insertConcepts(concepts)
    }

    private fun ConceptEntity.toDomain(): Concept {
        val type = object : TypeToken<Map<String, LanguageDetail>>() {}.type
        val langMap: Map<String, LanguageDetail> = try {
            gson.fromJson(this.languagesJson, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
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
