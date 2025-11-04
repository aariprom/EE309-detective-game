package com.ee309.detectivegame.llm.client

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Upstage API client interface
 * TODO: Update with actual Upstage API endpoints
 */
interface UpstageApiService {
    @POST("v1/chat/completions")
    suspend fun chatCompletion(@Body request: ChatRequest): ChatResponse
}

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 1000
)

data class ChatMessage(
    val role: String, // "system", "user", "assistant"
    val content: String
)

data class ChatResponse(
    val id: String,
    val choices: List<Choice>,
    val usage: Usage?
)

data class Choice(
    val message: ChatMessage,
    val finish_reason: String?
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

object UpstageApiClient {
    // TODO: Replace with actual Upstage API base URL
    private const val BASE_URL = "https://api.upstage.ai/"
    
    fun createService(apiKey: String): UpstageApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()
        
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UpstageApiService::class.java)
    }
}

