package com.ilmezubaan.app.data.local.dao

import androidx.room.*
import com.ilmezubaan.app.data.local.entities.LanguageMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LanguageMetadataDao {
    @Query("SELECT * FROM language_metadata WHERE languageId = :langId")
    fun getMetadata(langId: String): Flow<LanguageMetadataEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadata(metadata: LanguageMetadataEntity)
}
