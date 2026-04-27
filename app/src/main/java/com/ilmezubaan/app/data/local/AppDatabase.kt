package com.ilmezubaan.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ilmezubaan.app.data.local.converters.LanguageConverter
import com.ilmezubaan.app.data.local.dao.UserStatsDao
import com.ilmezubaan.app.data.local.dao.ConceptDao
import com.ilmezubaan.app.data.local.dao.LanguageMetadataDao
import com.ilmezubaan.app.data.local.entities.UserStats
import com.ilmezubaan.app.data.local.entities.ConceptEntity
import com.ilmezubaan.app.data.local.entities.LanguageMetadataEntity

@Database(
    entities = [UserStats::class, ConceptEntity::class, LanguageMetadataEntity::class],
    version = 10,
    exportSchema = true
)
@TypeConverters(LanguageConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userStatsDao(): UserStatsDao
    abstract fun conceptDao(): ConceptDao
    abstract fun languageMetadataDao(): LanguageMetadataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ilmezubaan_database"
                )
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
