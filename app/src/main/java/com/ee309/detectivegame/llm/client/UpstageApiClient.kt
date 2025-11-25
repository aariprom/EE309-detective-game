package com.ee309.detectivegame.llm.client

import com.ee309.detectivegame.BuildConfig
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Upstage API client interface
 */
interface UpstageApiService {
    @POST("chat/completions")
    suspend fun chatCompletion(@Body request: ChatRequest): ChatResponse
}

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double = 0.7,
    @SerializedName("max_tokens")
    val maxTokens: Int = 1000,
    val stream: Boolean = false
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
    @SerializedName("finish_reason")
    val finishReason: String?
)

data class Usage(
    @SerializedName("prompt_tokens")
    val promptTokens: Int,
    @SerializedName("completion_tokens")
    val completionTokens: Int,
    @SerializedName("total_tokens")
    val totalTokens: Int
)

object UpstageApiClient {
    fun createService(apiKey: String, baseUrl: String = BuildConfig.UPSTAGE_BASE_URL): UpstageApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
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
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UpstageApiService::class.java)
    }
}
