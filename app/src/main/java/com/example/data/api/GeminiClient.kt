package com.example.data.api

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<Content>
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<Candidate>?
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content?
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: GeminiApiService = retrofit.create(GeminiApiService::class.java)

    suspend fun generateSpiritualGuidance(prompt: String): String {
        // Accessing Gemini API key securely injected from BuildConfig via secrets gradle plugin
        val key = BuildConfig.GEMINI_API_KEY
        if (key.trim().isEmpty() || key == "MY_GEMINI_API_KEY" || key.contains("PLACEHOLDER")) {
            return "Spiritual Guide Offline Mode: API key is not configured in the AI Studio secrets panel. Please add a valid GEMINI_API_KEY.\n\nOffline suggestions:\n- Fast with absolute sincerity and humility.\n- Dedicate the day to charity and feed birds/hungry animals.\n- Sing prayers, clean the puja room, and light a ghee lamp."
        }
        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(Part(text = prompt))
                )
            )
        )
        return try {
            val response = apiService.generateContent(key, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "Vedic AI could not generate guidance at this moment. Please rephrase your request."
        } catch (e: Exception) {
            "The Spiritual AI Advisor is currently busy. Error details: ${e.localizedMessage ?: "Network exception"}.\n\nOffline suggestions:\n- Chant peace mantras (e.g., 'Om Shanti').\n- Maintain clean dietary intake (no onions, garlic, or wheat/rice grains for fasts).\n- Engage in self-reflection and study sacred books."
        }
    }
}
