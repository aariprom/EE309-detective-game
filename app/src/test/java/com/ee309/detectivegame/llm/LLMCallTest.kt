
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
    private val llm1_model = "solar-pro2"
    private val llm2_5_model = "solar-pro2"
    
    @Before
    fun setup() {
        if (apiKey.isNotBlank()) {
            upstageApiService = UpstageApiClient.createService(apiKey)
        }
    }

    @Test
    fun llm_chain_end_to_end_test() = runBlocking {
        if (!::upstageApiService.isInitialized) {
            println("API key not set. Skipping test.")
            return@runBlocking
        }

        // --- Test Keyword ---
        val keyword = "a murder mystery in a futuristic city called 'Neo-Alexandria'"

        // 1. LLM 1: Initializer
        println("--- Calling LLM 1: Initializer ---")
        val gameStructure = callLlm("You are a master storyteller. Create a complete game structure for a detective game based on the following theme: $keyword. Provide characters, places, clues, and a timeline in a structured JSON format.", llm1_model, 2048)
        println("LLM 1 Response (Game Structure):\n$gameStructure\n")
        
        // For the subsequent calls, we would parse the JSON from gameStructure.
        // For this test, we'''ll use placeholder values that you would normally extract from the gameStructure JSON.
        val placeholderCharacter = "a grizzled detective who has seen too much"
        val placeholderPlace = "a neon-drenched alleyway"
        val placeholderAction = "search the dumpster for clues"

        // 2. LLM 2: Dialogue Generator
        println("--- Calling LLM 2: Dialogue Generator ---")
        val dialogue = callLlm("You are playing the role of a character in a detective game. The character is '$placeholderCharacter'. A player asks you: 'What do you know about the incident?'. Respond naturally.", llm2_5_model)
        println("LLM 2 Response (Dialogue):\n$dialogue\n")

        // 3. LLM 3: Description Generator
        println("--- Calling LLM 3: Description Generator ---")
        val description = callLlm("Describe the following location in a detective game: '$placeholderPlace'. It is currently midnight and raining.", llm2_5_model)
        println("LLM 3 Response (Description):\n$description\n")

        // 4. LLM 4: Action Handler
        println("--- Calling LLM 4: Action Handler ---")
        val actionOutcome = callLlm("A player in a detective game wants to perform the following action: '$placeholderAction'. Determine if this is a valid action and describe the outcome.", llm2_5_model)
        println("LLM 4 Response (Action Outcome):\n$actionOutcome\n")
        
        // 5. LLM 5: Component Updater
        println("--- Calling LLM 5: Component Updater ---")
        val componentUpdate = callLlm("In a detective game, a timeline event has occurred: 'The power suddenly goes out in the district'. How does this affect the '$placeholderPlace' and the '$placeholderCharacter'?", llm2_5_model)
        println("LLM 5 Response (Component Update):\n$componentUpdate\n")
    }

    private suspend fun callLlm(prompt: String, model: String, maxTokens: Int = 500): String {
        try {
            val request = ChatRequest(
                model = model,
                messages = listOf(ChatMessage(role = "user", content = prompt)),
                maxTokens = maxTokens
            )
            val response = upstageApiService.chatCompletion(request)
            return response.choices.firstOrNull()?.message?.content ?: "No response from LLM."
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            println("Http ${e.code()} error: $errorBody")
            return "Http ${e.code()} error: $errorBody"
        } catch (e: Exception) {
            println("Error: ${e.message}")
            return "Error: ${e.message}"
        }
    }
}
