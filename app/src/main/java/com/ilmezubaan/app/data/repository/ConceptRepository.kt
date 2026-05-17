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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ConceptRepository(
    private val conceptDao: ConceptDao,
    private val metadataDao: LanguageMetadataDao,
    private val firebaseDatabase: FirebaseDatabase,
    private val auth: FirebaseAuth
) {
    private val gson = Gson()
    private val dbRef = firebaseDatabase.reference

    val allConcepts: Flow<List<Concept>> = conceptDao.getAllConcepts().map { entities ->
        entities.map { it.toDomain() }
    }.flowOn(Dispatchers.Default)

    private suspend fun signInAnonymouslyIfNeeded() {
        if (auth.currentUser == null) {
            try {
                auth.signInAnonymously().await()
                Log.d("FirebaseSync", "Signed in anonymously: ${auth.currentUser?.uid}")
            } catch (e: Exception) {
                Log.w("FirebaseSync", "Anonymous sign-in failed; trying public database read. " +
                        "Note: Ensure 'Anonymous' provider is enabled in Firebase Console > Authentication > Sign-in method.", e)
            }
        }
    }

    fun getMetadata(langId: String): Flow<LanguageMetadata?> {
        return metadataDao.getMetadata(langId).map { it?.toDomain() }
    }

    private var lastSyncTime = 0L
    private val SYNC_INTERVAL = 3600_000L // 1 hour
    private val syncMutex = Mutex()

    suspend fun syncConcepts(force: Boolean = false) = withContext(Dispatchers.IO) {
        syncMutex.withLock {
            if (!force && System.currentTimeMillis() - lastSyncTime < SYNC_INTERVAL) {
                Log.d("FirebaseSync", "Sync skipped: recently updated")
                return@withContext
            }
            try {
                Log.d("FirebaseSync", "Starting Sync... User: ${auth.currentUser?.uid}")
                signInAnonymouslyIfNeeded()

                val languagesSnapshot = dbRef.child("Languages").get().await()
                val snapshot = if (languagesSnapshot.exists()) languagesSnapshot else dbRef.get().await()

                if (!snapshot.exists()) {
                    Log.e("FirebaseSync", "Database is empty!")
                    return@withContext
                }

                val allRemoteConcepts = mutableListOf<ConceptEntity>()

                snapshot.children.forEach { langFolder ->
                    val langName = langFolder.key?.lowercase() ?: return@forEach
                    if (langName == "language_metadata" || langName == "users" || langName == "languages") return@forEach

                    langFolder.children.forEach { conceptSnapshot ->
                        val data = conceptSnapshot
                        val english = data.firstString("english_meaning", "englishMeaning", "english", "meaning")

                        if (english.isNotEmpty()) {
                            val id = "${langName}_${data.key}"
                            val category = data.firstString("category").ifBlank { "General" }
                            val level = data.firstString("level", "difficultyLevel").ifBlank { "Basic" }
                            val context = data.firstString("context").ifBlank { null }
                            val audioUrl = data.firstString("audio_url", "audioUrl")
                            val mediaUrl = data.firstString("media_url", "mediaUrl", "video_url", "videoUrl").ifBlank { audioUrl }
                            val type = data.firstString("type", "lesson_type").ifBlank { 
                                if (mediaUrl.contains(".mp4", ignoreCase = true)) "VIDEO" else "AUDIO" 
                            }

                            val example = data.firstString("${langName}_example", "example")

                            val exampleMeaning = data.firstString("${langName}_example_meaning", "example_meaning", "exampleMeaning")

                            val languages = mutableMapOf<String, LanguageDetail>()

                            val nativeScript = data.firstString(
                                "${langName}_shahmukhi",
                                "${langName}_script",
                                "urdu_meaning",
                                "native_script",
                                "script",
                                "word"
                            )

                            languages[langName] = LanguageDetail(
                                script = nativeScript,
                                roman = data.firstString("roman", "pronunciation", "transliteration"),
                                type = type,
                                mediaUrl = mediaUrl,
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
                                    context = context,
                                    updatedAt = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                }

                if (allRemoteConcepts.isNotEmpty()) {
                    conceptDao.insertConcepts(allRemoteConcepts)
                    lastSyncTime = System.currentTimeMillis()
                    Log.d("FirebaseSync", "Successfully synced ${allRemoteConcepts.size} concepts.")
                } else {
                    Log.e("FirebaseSync", "Sync finished with zero concepts. Check Firebase field names and read rules.")
                }
            } catch (e: Exception) {
                Log.e("FirebaseSync", "Sync failed: ${e.message}", e)
            }
        }
    }

    suspend fun syncMetadata() = withContext(Dispatchers.IO) {
        try {
            signInAnonymouslyIfNeeded()
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
            context = this.context,
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

    private fun com.google.firebase.database.DataSnapshot.firstString(vararg keys: String): String {
        for (key in keys) {
            val value = child(key).value?.toString()?.trim()
            if (!value.isNullOrEmpty() && value != "null") return value
        }
        return ""
    }
}
