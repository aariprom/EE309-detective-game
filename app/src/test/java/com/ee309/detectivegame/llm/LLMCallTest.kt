
package com.ee309.detectivegame.llm

import com.ee309.detectivegame.BuildConfig
import com.ee309.detectivegame.llm.client.ChatMessage
import com.ee309.detectivegame.llm.client.ChatRequest
import com.ee309.detectivegame.llm.client.UpstageApiClient
import com.ee309.detectivegame.llm.client.UpstageApiService
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class LLMCallTest {

    private lateinit var upstageApiService: UpstageApiService

    // --- CONFIGURATION ---
    private val apiKey = BuildConfig.UPSTAGE_API_KEY

    val systemPrompt = ChatMessage(
        role = "system",
        content = "Always start the message with ***TEST***"
    )

    val userPrompt = ChatMessage(
        role = "user",
        content = "What is 1 + 1?"
    )

    @Before
    fun setup() {
        if (apiKey.isNotBlank()) {
            upstageApiService = UpstageApiClient.createService(apiKey)
        }
    }

    @Test
    fun llm_call_test() = runBlocking {
        if (!::upstageApiService.isInitialized) {
            println("API key not set. Skipping test.")
            return@runBlocking
        }

        try {
            val request = ChatRequest(
                model = "solar-pro2",
                messages = listOf(systemPrompt, userPrompt),
                maxTokens = 500,
                responseFormat = null
            )
            val response = upstageApiService.chatCompletion(request)
            println(response.choices.firstOrNull()?.message?.content ?: "No response from LLM.")
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            println("Http ${e.code()} error: $errorBody")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
}
