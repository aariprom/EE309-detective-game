package com.ee309.detectivegame.llm.data

import com.ee309.detectivegame.llm.client.ChatMessage
import com.ee309.detectivegame.llm.client.ChatRequest
import com.ee309.detectivegame.llm.client.UpstageApiService
import com.ee309.detectivegame.llm.config.LLMTask
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LLMRepository @Inject constructor(
    private val upstageApiService: UpstageApiService
) {

    // Every call to Upstage API must be through this function
    suspend fun callUpstage(
        task: LLMTask,
        userContent: String,
        maxTokens: Int = 1000
    ): String {
        val messages = listOf(
            ChatMessage(role = "system", content = task.systemPrompt),
            ChatMessage(role = "user", content = userContent)
        )

        val responseFormatJson: JsonObject? = task.schema?.let { schema ->
            val schemaJson = JsonParser.parseString(schema).asJsonObject

            JsonObject().apply {
                addProperty("type", "json_schema")
                add("json_schema", schemaJson)
            }
        }

        val request = ChatRequest(
            model = task.model,
            messages = messages,
            stream = false,
            maxTokens = maxTokens,
            responseFormat = responseFormatJson
        )
        println(request)
        
        try {
            val response = upstageApiService.chatCompletion(request)
            val content = response.choices.firstOrNull()?.message?.content
            if (content.isNullOrBlank()) {
                throw Exception("LLM returned empty response")
            }
            return content
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = "HTTP ${e.code()} error: ${errorBody ?: e.message}"
            println(errorMessage)
            throw Exception(errorMessage, e)
        } catch (e: java.net.SocketTimeoutException) {
            val errorMessage = "Request timeout: The server took too long to respond. Please try again."
            println(errorMessage)
            throw Exception(errorMessage, e)
        } catch (e: java.io.IOException) {
            val errorMessage = "Network error: ${e.message ?: "Unable to connect to server. Please check your internet connection."}"
            println(errorMessage)
            throw Exception(errorMessage, e)
        } catch (e: Exception) {
            // Re-throw if already wrapped, otherwise wrap it
            if (e.message != null && e.message!!.startsWith("HTTP") || 
                e.message != null && e.message!!.startsWith("Request timeout") ||
                e.message != null && e.message!!.startsWith("Network error")) {
                throw e
            }
            val errorMessage = "LLM API error: ${e.message ?: "Unknown error occurred"}"
            println(errorMessage)
            throw Exception(errorMessage, e)
        }
    }
}