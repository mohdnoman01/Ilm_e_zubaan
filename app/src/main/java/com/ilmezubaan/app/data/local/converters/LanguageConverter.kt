package com.ilmezubaan.app.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ilmezubaan.app.data.local.entities.ConceptLanguageData

class LanguageConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromLanguageMap(value: Map<String, ConceptLanguageData>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toLanguageMap(value: String): Map<String, ConceptLanguageData> {
        val mapType = object : TypeToken<Map<String, ConceptLanguageData>>() {}.type
        return gson.fromJson(value, mapType)
    }
}
