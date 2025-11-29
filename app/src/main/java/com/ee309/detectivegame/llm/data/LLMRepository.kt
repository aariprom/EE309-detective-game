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

        try {
            val request = ChatRequest(
                model = task.model,
                messages = messages,
                stream = false,
                maxTokens = maxTokens,
                responseFormat = responseFormatJson
            )
            println(request)
            val response = upstageApiService.chatCompletion(request)
            return response.choices.firstOrNull()?.message?.content ?: ""
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            println("Http ${e.code()} error: $errorBody")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        return ""
    }
}