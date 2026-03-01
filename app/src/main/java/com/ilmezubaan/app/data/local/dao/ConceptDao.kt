package com.ilmezubaan.app.data.local.dao

import androidx.room.*
import com.ilmezubaan.app.data.local.entities.ConceptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConceptDao {
    @Query("SELECT * FROM concepts")
    fun getAllConcepts(): Flow<List<ConceptEntity>>

    @Query("SELECT * FROM concepts WHERE conceptId = :id")
    suspend fun getConceptById(id: String): ConceptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConcepts(concepts: List<ConceptEntity>)

    @Query("DELETE FROM concepts")
    suspend fun deleteAll()
}
