package com.ilmezubaan.app.data.remote.gemini

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    fun createService(): GeminiApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }
}
