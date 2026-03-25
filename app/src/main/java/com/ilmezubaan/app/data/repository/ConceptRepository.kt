package com.ilmezubaan.app.data.repository

import android.util.Log
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ConceptRepository(
    private val conceptDao: ConceptDao,
    private val metadataDao: LanguageMetadataDao,
    private val firebaseDatabase: FirebaseDatabase
) {
    private val gson = Gson()
    private val dbRef = firebaseDatabase.reference

    val allConcepts: Flow<List<Concept>> = conceptDao.getAllConcepts().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getMetadata(langId: String): Flow<LanguageMetadata?> {
        return metadataDao.getMetadata(langId).map { it?.toDomain() }
    }

    suspend fun syncConcepts() {
        try {
            Log.d("FirebaseSync", "Starting Sync...")
            
            // Try fetching from root to see what's actually there
            val snapshot = dbRef.get().await()
            
            if (!snapshot.exists()) {
                Log.e("FirebaseSync", "Database is empty!")
                return
            }

            // If 'Languages' exists, use it. Otherwise, use the root snapshot.
            val languagesNode = snapshot.child("Languages")
            val targetSnapshot = if (languagesNode.exists()) {
                Log.d("FirebaseSync", "Found 'Languages' node.")
                languagesNode
            } else {
                Log.d("FirebaseSync", "'Languages' node not found, using root.")
                snapshot
            }

            Log.d("FirebaseSync", "Target Node: ${targetSnapshot.key}, Children: ${targetSnapshot.childrenCount}")

            val allRemoteConcepts = mutableListOf<ConceptEntity>()

            targetSnapshot.children.forEach { langFolder ->
                val langName = langFolder.key?.lowercase() ?: return@forEach
                
                // Skip non-language metadata/user folders if we are at root
                if (langName == "language_metadata" || langName == "users" || langName == "languages") return@forEach

                Log.d("FirebaseSync", "Processing language: $langName")

                langFolder.children.forEach { conceptSnapshot ->
                    val data = conceptSnapshot
                    
                    // Check multiple possible keys for english meaning
                    val english = data.child("english_meaning").value?.toString()
                        ?: data.child("english").value?.toString()
                        ?: ""
                    
                    if (english.isNotEmpty()) {
                        val id = "${langName}_${data.key}"
                        val category = data.child("category").value?.toString() ?: "General"
                        val level = data.child("level").value?.toString() ?: data.child("difficulty").value?.toString() ?: "Basic"
                        val audioUrl = data.child("audio_url").value?.toString() ?: ""
                        
                        val example = data.child("${langName}_example").value?.toString()
                            ?: data.child("example").value?.toString()
                            ?: data.child("exampleMeaning").value?.toString()
                        
                        val exampleMeaning = data.child("${langName}_example_meaning").value?.toString()
                            ?: data.child("example_meaning").value?.toString()
                            ?: ""
                        
                        val languages = mutableMapOf<String, LanguageDetail>()
                        
                        // Try all possible native script keys
                        val nativeScript = data.child("${langName}_shahmukhi").value?.toString()
                            ?: data.child("${langName}_script").value?.toString()
                            ?: data.child("urdu_meaning").value?.toString()
                            ?: data.child("native_script").value?.toString()
                            ?: data.child("punjabi_shahmukhi").value?.toString()
                            ?: data.child("${langName}_urdu_meaning").value?.toString()
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
            } else {
                Log.e("FirebaseSync", "No concepts were parsed! Check if the data structure matches.")
            }
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Sync failed: ${e.message}", e)
        }
    }

    suspend fun syncMetadata() {
        try {
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

    suspend fun insertConcepts(concepts: List<ConceptEntity>) {
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
